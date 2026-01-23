package com.jobtracking.config;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.jobtracking.auth.entity.User;
import com.jobtracking.auth.repository.UserRepository;
import com.jobtracking.job.entity.Job;
import com.jobtracking.job.repository.JobRepository;
import com.jobtracking.organization.entity.Organization;
import com.jobtracking.organization.repository.OrganizationRepository;

import lombok.RequiredArgsConstructor;

@Component
@Profile({"dev", "local"}) // Only run in development/local environments
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OrganizationRepository organizationRepository;
    private final JobRepository jobRepository;

    @Override
    public void run(String... args) throws Exception {
        // Create test users if they don't exist
        createTestUsers();
        // Create test companies and jobs
        createTestData();
    }

    private void createTestUsers() {
        try {
            // Create admin user
            if (!userRepository.existsByEmail("admin@jobtracking.com")) {
                User admin = User.builder()
                        .username("admin")
                        .email("admin@jobtracking.com")
                        .passwordHash(passwordEncoder.encode("admin123"))
                        .roleId(1) // Admin role
                        .fullname("System Administrator")
                        .active(true)
                        .build();
                userRepository.save(admin);
            }

            // Create recruiter user
            if (!userRepository.existsByEmail("chaitanya@gmail.com")) {
                User recruiter = User.builder()
                        .username("chaitanya")
                        .email("chaitanya@gmail.com")
                        .passwordHash(passwordEncoder.encode("chaitanya@123"))
                        .roleId(2) // Recruiter role
                        .fullname("Chaitanya Sista")
                        .active(true)
                        .build();
                userRepository.save(recruiter);
            }

            // Create job seeker user
            if (!userRepository.existsByEmail("jobseeker@gmail.com")) {
                User jobSeeker = User.builder()
                        .username("jobseeker")
                        .email("jobseeker@gmail.com")
                        .passwordHash(passwordEncoder.encode("jobseeker123"))
                        .roleId(3) // Job seeker role
                        .fullname("Test Job Seeker")
                        .active(true)
                        .build();
                userRepository.save(jobSeeker);
            }
        } catch (Exception e) {
            // Handle initialization errors silently
        }
    }

    private void createTestData() {
        try {
            // Create test companies
            if (organizationRepository.count() == 0) {
                Organization techSoft = new Organization();
                techSoft.setName("TechSoft Pvt Ltd");
                techSoft.setWebsite("https://techsoft.com");
                techSoft.setCity("Bangalore");
                techSoft.setContactEmail("hr@techsoft.com");
                techSoft.setDescription("Leading software development company");
                techSoft.setVerified(true);
                techSoft.setRecruiterUserId(2L); // Recruiter user ID
                organizationRepository.save(techSoft);

                Organization dataWorks = new Organization();
                dataWorks.setName("DataWorks Inc");
                dataWorks.setWebsite("https://dataworks.com");
                dataWorks.setCity("Mumbai");
                dataWorks.setContactEmail("careers@dataworks.com");
                dataWorks.setDescription("Data analytics and AI solutions");
                dataWorks.setVerified(true);
                dataWorks.setRecruiterUserId(2L); // Recruiter user ID
                organizationRepository.save(dataWorks);
            }

            // Create test jobs
            if (jobRepository.count() == 0) {
                Job job1 = new Job();
                job1.setTitle("Senior Java Developer");
                job1.setDescription("Experienced Java developer needed for enterprise applications");
                job1.setMinSalary(80000.0);
                job1.setMaxSalary(120000.0);
                job1.setLocation("Bangalore");
                job1.setJobType("Full-time");
                job1.setCompanyId(1L); // TechSoft company ID
                job1.setRecruiterUserId(2L); // Recruiter user ID
                job1.setIsActive(true);
                jobRepository.save(job1);

                Job job2 = new Job();
                job2.setTitle("Frontend React Developer");
                job2.setDescription("React developer for modern web applications");
                job2.setMinSalary(60000.0);
                job2.setMaxSalary(90000.0);
                job2.setLocation("Mumbai");
                job2.setJobType("Full-time");
                job2.setCompanyId(2L); // DataWorks company ID
                job2.setRecruiterUserId(2L); // Recruiter user ID
                job2.setIsActive(true);
                jobRepository.save(job2);

                Job job3 = new Job();
                job3.setTitle("Data Analyst");
                job3.setDescription("Analyze business data and create insights");
                job3.setMinSalary(50000.0);
                job3.setMaxSalary(75000.0);
                job3.setLocation("Mumbai");
                job3.setJobType("Full-time");
                job3.setCompanyId(2L); // DataWorks company ID
                job3.setRecruiterUserId(2L); // Recruiter user ID
                job3.setIsActive(true);
                jobRepository.save(job3);

                Job job4 = new Job();
                job4.setTitle("Full Stack Developer");
                job4.setDescription("Full stack development with modern technologies");
                job4.setMinSalary(70000.0);
                job4.setMaxSalary(100000.0);
                job4.setLocation("Bangalore");
                job4.setJobType("Full-time");
                job4.setCompanyId(1L); // TechSoft company ID
                job4.setRecruiterUserId(2L); // Recruiter user ID
                job4.setIsActive(true);
                jobRepository.save(job4);

                Job job5 = new Job();
                job5.setTitle("UI/UX Designer");
                job5.setDescription("Design user interfaces and user experiences");
                job5.setMinSalary(45000.0);
                job5.setMaxSalary(70000.0);
                job5.setLocation("Mumbai");
                job5.setJobType("Full-time");
                job5.setCompanyId(2L); // DataWorks company ID
                job5.setRecruiterUserId(2L); // Recruiter user ID
                job5.setIsActive(true);
                jobRepository.save(job5);
            }
        } catch (Exception e) {
            // Handle test data creation errors silently
        }
    }
}