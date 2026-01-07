package com.xypha.onlineBus.account.users.service;


import com.xypha.onlineBus.account.role.Role;
import com.xypha.onlineBus.account.users.dto.UserRequest;
import com.xypha.onlineBus.account.users.dto.UserResponse;
import com.xypha.onlineBus.account.users.entity.User;
import com.xypha.onlineBus.account.users.mapper.UserMapper;
import com.xypha.onlineBus.account.users.mapper.UserMapperUtil;
import com.xypha.onlineBus.api.ApiResponse;
import com.xypha.onlineBus.api.PaginatedResponse;
import com.xypha.onlineBus.booking.services.BookingEmailService;
import com.xypha.onlineBus.error.BadRequestException;
import com.xypha.onlineBus.mail.EmailService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {


    private final UserMapper userMapper;

    private final PasswordEncoder encoder;
    private final BookingEmailService bookingEmailService;
    private final EmailService emailService;

    public UserService(UserMapper userMapper, PasswordEncoder encoder, BookingEmailService bookingEmailService, EmailService emailService) {
        this.userMapper = userMapper;
        this.encoder = encoder;
        this.bookingEmailService = bookingEmailService;
        this.emailService = emailService;
    }

    public UserResponse createUser(UserRequest request) {

        Role role = request.getRole() != null ? request.getRole() : Role.USER;
        if (role == Role.USER) {
            if (request.getUsername() == null ||
                    request.getGmail() == null ||
                    request.getPhoneNumber() == null ||
                    request.getNrc() == null ||
                    request.getDob() == null ||
                    request.getCitizenship() == null) {
                throw new BadRequestException("All personal details are required for regular users");
            }
        }

        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new BadRequestException("Password is required");
        }

        // 2️⃣ Email validation
        if (!isGmail(request.getGmail())) {
            throw new BadRequestException("Email must be a Gmail account");
        }

        if (!isEmailExists(request.getGmail())) {
            throw new BadRequestException("Email domain cannot receive emails");
        }

        // 3️⃣ NRC validation
        if (!isValidNrc(request.getNrc())) {
            throw new BadRequestException("Invalid NRC format. Must be like 00/DAGANA(N)000000");
        }

        // 4️⃣ Phone validation
        if (!isValidPhoneNumber(request.getPhoneNumber())) {
            throw new BadRequestException("Invalid phone number format. Must start with 09 or 959");
        }

        // 5️⃣ Uniqueness checks
        if (userMapper.findByEmail(request.getGmail()) != null) {
            throw new BadRequestException("Email already exists");
        }
        if (userMapper.findByPhoneNumber(request.getPhoneNumber()) != null) {
            throw new BadRequestException("Phone Number already exists");
        }
        if (userMapper.findByNrc(request.getNrc()) != null) {
            throw new BadRequestException("NRC already exists");
        }

        User user = UserMapperUtil.toEntity(request);
        user.setRole(role);
        user.setPassword(encoder.encode(user.getPassword()));

        userMapper.insertUser(user);
        bookingEmailService.sendVerificationEmail(user.getGmail(), user.getUsername());

        return UserMapperUtil.toDTO(userMapper.getUserById(user.getId()));
    }

    public UserResponse getUserById(Long id) {
        User user = userMapper.getUserById(id);
        return user != null ? UserMapperUtil.toDTO(user) : null;
    }

    public UserResponse updateUser(Long id, UserRequest request) {
        User existsUser = userMapper.getUserById(id);
        if (existsUser == null) {
            return null;
        }
        existsUser.setUsername(request.getUsername());
        existsUser.setGmail(request.getGmail());
        existsUser.setPhoneNumber(request.getPhoneNumber());
        existsUser.setNrc(request.getNrc());
        existsUser.setGender(request.getGender());
        existsUser.setDob(request.getDob());
        existsUser.setCitizenship(request.getCitizenship());

        //update password
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            existsUser.setPassword(encoder.encode(request.getPassword()));
        }

        //update role
        if (request.getRole() != null) {
            existsUser.setRole(request.getRole());
        }
        userMapper.updateUser(existsUser);
        return UserMapperUtil.toDTO(userMapper.getUserById(id));
    }

    public boolean deleteUser(Long id) {
        User user = userMapper.getUserById(id);
        if (user == null)
            return false;

        userMapper.deleteUser(id);
        return true;
    }


    public ApiResponse<PaginatedResponse<UserResponse>> getAllUserPaginated(int offset, int limit, String roleFilter) {
        int offsets = offset * limit;
        List<User> users = userMapper.getUsersByRolePaginated(roleFilter, offset, limit);
        long total = userMapper.countUserByRole(roleFilter);

        List<UserResponse> userResponses = users.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        PaginatedResponse<UserResponse> paginatedResponse = new PaginatedResponse<>(offset,limit,total,userResponses);
        return new ApiResponse<>("SUCCESS","User retrieved successfully", paginatedResponse);
    }

    public UserResponse getUserByUsername(String username) {
        User user = userMapper.findByUsername(username);
        if (user == null) return null;
        return UserMapperUtil.toDTO(user);
    }

    public UserResponse updateUserByUsername(String username, UserRequest userRequest) {
        User existsUser = userMapper.findByUsername(username);

        if (existsUser == null) {
            throw new BadRequestException("User Not found");
        }
        existsUser.setUsername(userRequest.getUsername() != null ? userRequest.getUsername() : existsUser.getUsername());
        existsUser.setGmail(userRequest.getGmail() != null ? userRequest.getGmail() : existsUser.getGmail());
        existsUser.setPhoneNumber(userRequest.getPhoneNumber() != null ? userRequest.getPhoneNumber() : existsUser.getPhoneNumber());
        existsUser.setNrc(userRequest.getNrc() != null ? userRequest.getNrc() : existsUser.getNrc());
        existsUser.setGender(userRequest.getGender() != null ? userRequest.getGender() : existsUser.getGender());
        existsUser.setDob(userRequest.getDob() != null ? userRequest.getDob() : existsUser.getDob());
        existsUser.setCitizenship(userRequest.getCitizenship() != null ? userRequest.getCitizenship() : existsUser.getCitizenship());

        if (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty()) {
            existsUser.setPassword(encoder.encode(userRequest.getPassword()));
        }
        userMapper.updateUser(existsUser);
        return UserMapperUtil.toDTO(existsUser);

    }

    public User getUserEntityByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    public PaginatedResponse<UserResponse> searchUserByGmail (String gmail,int offset, int limit) {
        List<UserResponse> users = userMapper.searchUserByEmail(gmail, offset, limit);
        if (users.isEmpty()){
            return new PaginatedResponse<>(offset, limit, 0, List.of());
        }


        List<UserResponse> userResponses = users
                .stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getGmail(),
                        user.getPhoneNumber(),
                        user.getNrc(),
                        user.getGender(),
                        user.getDob(),
                        user.getCitizenship(),
                        user.getRole()
                ))
                .toList();
        long total = userMapper.countUsersByGmail(gmail);
        return new PaginatedResponse<>(offset, limit, total, users);

    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userMapper.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole().name()))
        );
    }

    private boolean isEmailExists(String email) {
        try{
            String domain = email.substring(email.indexOf("@") + 1);
            Hashtable<String, String> env = new Hashtable<>();
            env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            DirContext ictx = new InitialDirContext(env);
            Attributes attrs = ictx.getAttributes(domain, new String[]{"MX"});
            return attrs.get("MX") != null;
        } catch (NamingException e) {
            return false;
        }
    }
    private boolean isValidNrc(String nrc){
        if (nrc == null || nrc.isEmpty()){
            return false;
        }
        String nrcRegex = "^\\d{2}/[A-Z]+\\([A-Z]\\)\\d{6}$";
        return nrc.matches(nrcRegex);
    }
    private boolean isValidPhoneNumber (String phone){
        if (phone == null || phone.isEmpty()) return false;
        String phoneRegex = "^(09|959)\\d{7,9}$";
        return phone.matches(phoneRegex);
    }
    private boolean isGmail (String email){
        if (email == null || email.isEmpty()) return false;
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }


    private UserResponse mapToResponse (User user) {
        if (user == null) return null;

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getGmail(),
                user.getPhoneNumber(),
                user.getNrc(),
                user.getGender(),
                user.getDob(),
                user.getCitizenship(),
                user.getRole()
        );
    }
}

