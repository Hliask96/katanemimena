package com.house.demo;

import com.github.javafaker.Faker;
import com.house.demo.classes.*;
import com.house.demo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

@Component
public class InitialDataLoader implements
        ApplicationListener<ContextRefreshedEvent> {

    boolean alreadySetup = false;

    @Autowired
    private SettingsRepository settingsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private DepartmentRepository departmentRepository;


    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (alreadySetup)
            return;

        // setup roles and privileges
        Privilege see_all_users = new Privilege("see_all_users");
        privilegeRepository.save(see_all_users);

        Privilege create_update_user = new Privilege("create_update_user");
        privilegeRepository.save(create_update_user);


        Privilege see_all_students = new Privilege("see_all_students");
        privilegeRepository.save(see_all_students);

        Privilege update_house_limit = new Privilege("update_house_limit");
        privilegeRepository.save(update_house_limit);

        Privilege update_student_application_status = new Privilege("update_student_application_status");
        privilegeRepository.save(update_student_application_status);


        Role admin = new Role("admin");
        Role ipallilos = new Role("ipallilos");
        Role student = new Role("student");


        List<Privilege> adminPrivileges = Arrays.asList(see_all_users, update_house_limit);
        admin.setPrivileges(adminPrivileges);
        roleRepository.save(admin);

        List<Privilege> ipallilosPrivileges = Arrays.asList(see_all_students, create_update_user, update_student_application_status);
        ipallilos.setPrivileges(ipallilosPrivileges);
        roleRepository.save(ipallilos);

        List<Privilege> studentPrivileges = Arrays.asList();
        student.setPrivileges(studentPrivileges);
        roleRepository.save(student);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // create departments
        Department department1 = new Department();
        department1.setName("Informatics");
        departmentRepository.save(department1);

        Department department2 = new Department();
        department2.setName("Home Economics");
        departmentRepository.save(department2);

        Department department3 = new Department();
        department3.setName("Geography");
        departmentRepository.save(department3);

        Department department4 = new Department();
        department4.setName("Dietology");
        departmentRepository.save(department4);

        Department department5 = new Department();
        department5.setName("Tourism");
        departmentRepository.save(department5);

        // create admin
        User user = new User();
        user.setUsername("admin");
        user.setPassword(passwordEncoder.encode("admin"));
        user.setRoles(Arrays.asList(admin));
        userRepository.save(user);

        // create settings
        Settings settings = new Settings();
        settings.setHouse_limit(200);
        settingsRepository.save(settings);

        // create 4 ipallilous
        for (int i=0; i< 5; i++) {
            user = new User();

            Faker faker = new Faker();

            user.setUsername("ipallilos"+i);
            user.setFirstName(faker.name().firstName());
            user.setLastName(faker.name().lastName());
            user.setEmail(faker.internet().emailAddress());
            user.setPassword(passwordEncoder.encode("ipallilos"+i));
            user.setRoles(Arrays.asList(ipallilos));

            switch (i) {
                case 0:
                    user.setDepartment(department1);
                    break;
                case 1:
                    user.setDepartment(department2);
                    break;
                case 2:
                    user.setDepartment(department3);
                    break;
                case 3:
                    user.setDepartment(department4);
                    break;
                case 4:
                    user.setDepartment(department5);
                    break;
            }

            userRepository.save(user);
        }


        for (int i=0; i < 25; i++) {
            user = new User();
            System.out.println(i);
            Faker faker = new Faker();

            user.setUsername("mathitis"+i);
            user.setFirstName(faker.name().firstName());
            user.setLastName(faker.name().lastName());
            user.setEmail(faker.internet().emailAddress());
            user.setPassword(passwordEncoder.encode("mathitis"+i));
            user.setRoles(Arrays.asList(student));

            user.setDateOfBirth(faker.date().birthday().toString());


            if (i<5) {
                user.setDepartment(department1);
            }
            else if (i<10) {
                user.setDepartment(department2);
            }
            else if (i<15) {
                user.setDepartment(department3);
            }
            else if (i<20) {
                user.setDepartment(department4);
            }
            else if (i<25) {
                user.setDepartment(department5);
            }

            userRepository.save(user);

            Application application = new Application();
            application.setEisodima(faker.number().numberBetween(0, 50000));
            application.setYear(faker.number().numberBetween(1, 10));
            application.setAderfia_se_diaforetiki_poli(faker.number().numberBetween(1, 5));
            application.setAderfia_stin_idia_poli(faker.number().numberBetween(1, 5));
            application.setStatus(faker.number().numberBetween(0,2));
            application.setUser(user);
            applicationRepository.save(application);
        }

        user = new User();

        Faker faker = new Faker();

        user.setUsername("doe");
        user.setFirstName(faker.name().firstName());
        user.setLastName(faker.name().lastName());
        user.setEmail(faker.internet().emailAddress());
        user.setPassword(passwordEncoder.encode("doe"));
        user.setRoles(Arrays.asList(student));

        user.setDateOfBirth(faker.date().birthday().toString());

        user.setDepartment(department1);


        userRepository.save(user);



    }

}