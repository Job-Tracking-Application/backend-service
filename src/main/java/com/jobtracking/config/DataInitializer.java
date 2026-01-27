package com.jobtracking.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.jobtracking.application.entity.Application;
import com.jobtracking.application.enums.ApplicationStatus;
import com.jobtracking.application.repository.ApplicationRepository;
import com.jobtracking.auth.entity.User;
import com.jobtracking.auth.repository.UserRepository;
import com.jobtracking.job.entity.Job;
import com.jobtracking.job.repository.JobRepository;
import com.jobtracking.organization.repository.OrganizationRepository;
import com.jobtracking.organization.service.OrganizationService;
import com.jobtracking.organization.dto.OrganizationRequest;
import com.jobtracking.profile.entity.JobSeekerProfile;
import com.jobtracking.profile.entity.JobSeekerSkill;
import com.jobtracking.profile.entity.Skill;
import com.jobtracking.profile.enums.Proficiency;
import com.jobtracking.profile.repository.JobSeekerProfileRepository;
import com.jobtracking.profile.repository.JobSeekerSkillsRepository;
import com.jobtracking.profile.repository.SkillRepository;

import lombok.RequiredArgsConstructor;

@Component
@Profile({"dev", "local", "test"}) // Only run in development/local/test environments, NOT in production
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OrganizationRepository organizationRepository;
    private final OrganizationService organizationService;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final SkillRepository skillRepository;
    private final JobSeekerSkillsRepository jobSeekerSkillsRepository;

    @Override
    public void run(String... args) throws Exception {
        // Create test users if they don't exist
        createTestUsers();
        // Create test companies and jobs
        createTestData();
        // Create test applications
        createTestApplications();
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

            // Create second recruiter user
            if (!userRepository.existsByEmail("recruiter2@gmail.com")) {
                User recruiter2 = User.builder()
                        .username("recruiter2")
                        .email("recruiter2@gmail.com")
                        .passwordHash(passwordEncoder.encode("recruiter123"))
                        .roleId(2) // Recruiter role
                        .fullname("Second Recruiter")
                        .active(true)
                        .build();
                userRepository.save(recruiter2);
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
            // Get the actual recruiter user IDs
            User recruiterUser1 = userRepository.findByEmail("chaitanya@gmail.com").orElse(null);
            User recruiterUser2 = userRepository.findByEmail("recruiter2@gmail.com").orElse(null);
            
            if (recruiterUser1 == null) {
                return; // Can't create companies without recruiter
            }
            
            Long recruiterId1 = recruiterUser1.getId();
            Long recruiterId2 = recruiterUser2 != null ? recruiterUser2.getId() : recruiterId1;
            
            // Create test companies using the service layer to enforce business rules
            if (organizationRepository.count() == 0) {
                try {
                    // Create company for first recruiter
                    OrganizationRequest techSoftRequest = new OrganizationRequest(
                        "TechSoft Pvt Ltd",
                        "https://techsoft.com", 
                        "Bangalore",
                        "hr@techsoft.com",
                        "Leading software development company",
                        null
                    );
                    organizationService.createOrganization(techSoftRequest, recruiterId1);
                    
                    // Create company for second recruiter (if exists)
                    if (recruiterUser2 != null) {
                        OrganizationRequest dataWorksRequest = new OrganizationRequest(
                            "DataWorks Inc",
                            "https://dataworks.com",
                            "Mumbai", 
                            "careers@dataworks.com",
                            "Data analytics and AI solutions",
                            null
                        );
                        organizationService.createOrganization(dataWorksRequest, recruiterId2);
                    }
                } catch (Exception e) {
                    // Companies might already exist, continue
                }
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
                job1.setRecruiterUserId(recruiterId1); // First recruiter
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
                job2.setRecruiterUserId(recruiterId2); // Second recruiter
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
                job3.setRecruiterUserId(recruiterId2); // Second recruiter
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
                job4.setRecruiterUserId(recruiterId1); // First recruiter
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
                job5.setRecruiterUserId(recruiterId2); // Second recruiter
                job5.setIsActive(true);
                jobRepository.save(job5);
            }
        } catch (Exception e) {
            // Handle test data creation errors silently
        }
    }

    private void createTestApplications() {
        try {
            // Get the job seeker user
            User jobSeeker = userRepository.findByEmail("jobseeker@gmail.com").orElse(null);
            if (jobSeeker == null) return;

            // Create or get job seeker profile
            JobSeekerProfile profile = jobSeekerProfileRepository.findByUserId(jobSeeker.getId())
                .orElseGet(() -> {
                    JobSeekerProfile newProfile = new JobSeekerProfile();
                    newProfile.setUser(jobSeeker);
                    newProfile.setBioEn("Experienced software developer with passion for creating innovative solutions");
                    newProfile.setEducation("{\"degree\":\"Bachelor's in Computer Science\",\"college\":\"Tech University\",\"year\":2020}");
                    newProfile.setResumeLink("https://example.com/resume-link");
                    return jobSeekerProfileRepository.save(newProfile);
                });

            // Update existing profile if education is not in JSON format
            if (profile.getEducation() != null && !profile.getEducation().startsWith("{")) {
                profile.setBioEn("Experienced software developer with passion for creating innovative solutions");
                profile.setEducation("{\"degree\":\"Bachelor's in Computer Science\",\"college\":\"Tech University\",\"year\":2020}");
                profile.setResumeLink("https://example.com/resume-link");
                jobSeekerProfileRepository.save(profile);
            }

            // Always create initial skills (check if skills exist first)
            createInitialSkills(profile);

            // Create applications only if none exist
            if (applicationRepository.count() == 0) {
                // Get jobs to apply for (use findById to get actual jobs from DB)
                List<Job> allJobs = jobRepository.findAll();
                if (allJobs.size() >= 3) {
                    Job job1 = allJobs.get(0);
                    Job job2 = allJobs.get(1);
                    Job job3 = allJobs.get(2);

                    // Create applications
                    if (job1 != null) {
                        Application app1 = new Application();
                        app1.setUser(jobSeeker);
                        app1.setJob(job1);
                        app1.setStatus(ApplicationStatus.APPLIED);
                        app1.setResumePath("https://example.com/resume-link");
                        app1.setCoverLetter("I am very interested in this Java Developer position and believe my skills align well with your requirements.");
                        applicationRepository.save(app1);
                    }

                    if (job2 != null) {
                        Application app2 = new Application();
                        app2.setUser(jobSeeker);
                        app2.setJob(job2);
                        app2.setStatus(ApplicationStatus.INTERVIEWED);
                        app2.setResumePath("https://example.com/resume-link");
                        app2.setCoverLetter("I have extensive experience with React and would love to contribute to your frontend team.");
                        applicationRepository.save(app2);
                    }

                    if (job3 != null) {
                        Application app3 = new Application();
                        app3.setUser(jobSeeker);
                        app3.setJob(job3);
                        app3.setStatus(ApplicationStatus.APPLIED);
                        app3.setResumePath("https://example.com/resume-link");
                        app3.setCoverLetter("My analytical skills and experience with data visualization make me a great fit for this role.");
                        applicationRepository.save(app3);
                    }
                }
            }
        } catch (Exception e) {
            // Error creating test applications - continue silently
        }
    }

    private void createInitialSkills(JobSeekerProfile profile) {
        try {
            // Create comprehensive skills list first (regardless of profile)
            createAllSkills();
            
            // Check if skills already exist for this profile
            if (jobSeekerSkillsRepository.findByJobSeekerProfile(profile).isEmpty()) {
                String[] skillNames = {"Java", "Spring Boot", "React", "JavaScript", "MySQL", "Git", "REST APIs", "HTML/CSS"};
                
                for (String skillName : skillNames) {
                    try {
                        // Create or get skill
                        Skill skill = skillRepository.findByName(skillName)
                            .orElseGet(() -> {
                                Skill newSkill = new Skill();
                                newSkill.setName(skillName);
                                return skillRepository.save(newSkill);
                            });
                        
                        // Create job seeker skill association
                        JobSeekerSkill jobSeekerSkill = new JobSeekerSkill();
                        jobSeekerSkill.setJobSeekerProfile(profile);
                        jobSeekerSkill.setSkill(skill);
                        jobSeekerSkill.setProficiency(Proficiency.INTERMEDIATE); // Default proficiency
                        jobSeekerSkillsRepository.save(jobSeekerSkill);
                        
                    } catch (Exception e) {
                        // Error creating individual skill - continue with others
                    }
                }
                
            }
        } catch (Exception e) {
            // Error in createInitialSkills - continue silently
        }
    }

    private void createAllSkills() {
        try {
            String[] allSkills = {
                // Programming Languages
                "Java", "Python", "JavaScript", "TypeScript", "C#", "C++", "PHP", "Ruby", "Go", "Kotlin", "Swift",
                
                // Frontend Technologies
                "React", "Angular", "Vue.js", "HTML/CSS", "Bootstrap", "Tailwind CSS", "jQuery", "SASS/SCSS",
                
                // Backend Technologies
                "Spring Boot", "Node.js", "Express.js", "Django", "Flask", "ASP.NET", "Laravel", "Ruby on Rails",
                
                // Databases
                "MySQL", "PostgreSQL", "MongoDB", "Redis", "Oracle", "SQL Server", "SQLite", "Cassandra",
                
                // Cloud & DevOps
                "AWS", "Azure", "Google Cloud", "Docker", "Kubernetes", "Jenkins", "Git", "GitHub", "GitLab",
                
                // Tools & Frameworks
                "REST APIs", "GraphQL", "Microservices", "Spring Framework", "Hibernate", "JPA", "Maven", "Gradle",
                
                // Testing
                "JUnit", "Mockito", "Jest", "Cypress", "Selenium", "TestNG",
                
                // Other Skills
                "Agile", "Scrum", "Project Management", "UI/UX Design", "Data Analysis", "Machine Learning", "AI"
            };
            
            for (String skillName : allSkills) {
                try {
                    if (!skillRepository.findByName(skillName).isPresent()) {
                        Skill skill = new Skill();
                        skill.setName(skillName);
                        skillRepository.save(skill);
                    }
                } catch (Exception e) {
                    // Continue with other skills
                }
            }
        } catch (Exception e) {
            // Error creating skills - continue silently
        }
    }
}