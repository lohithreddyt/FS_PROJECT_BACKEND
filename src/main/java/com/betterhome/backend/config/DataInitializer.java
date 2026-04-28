package com.betterhome.backend.config;

import com.betterhome.backend.model.AppUser;
import com.betterhome.backend.model.ListingStatus;
import com.betterhome.backend.model.PropertyListing;
import com.betterhome.backend.model.Recommendation;
import com.betterhome.backend.model.Role;
import com.betterhome.backend.repository.AppUserRepository;
import com.betterhome.backend.repository.PropertyListingRepository;
import com.betterhome.backend.repository.RecommendationRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedData(
            AppUserRepository userRepository,
            PropertyListingRepository listingRepository,
            RecommendationRepository recommendationRepository
    ) {
        return args -> {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            if (userRepository.count() == 0) {
                userRepository.saveAll(List.of(
                        new AppUser("Demo User", "user@betterhome.in", encoder.encode("user123"), Role.USER),
                        new AppUser("Admin Verma", "admin@betterhome.in", encoder.encode("admin123"), Role.ADMIN)
                ));
            }

            if (recommendationRepository.count() == 0) {
                recommendationRepository.saveAll(List.of(
                        new Recommendation("Interior", "Modular Kitchen Upgrade", "Sleek modular units that improve functionality and appeal significantly.", "+18% ROI", "Rs 80K-1.5L", "Refresh worktops, cabinets, and task lighting."),
                        new Recommendation("Exterior", "Facade and Landscaping", "Fresh exterior paint and greenery create strong first impressions.", "+12% ROI", "Rs 30K-60K", "Focus on entryway paint, path cleanup, and low-maintenance plants."),
                        new Recommendation("Smart Home", "Smart Lighting and Automation", "Smart switches and LED lighting add convenience for modern buyers.", "+9% ROI", "Rs 25K-50K", "Add app-based controls, secure entry lighting, and timers."),
                        new Recommendation("Vastu", "Vastu-Compliant Redesign", "Vastu-aligned room arrangements increase buyer confidence.", "+15% ROI", "Rs 20K-40K", "Rebalance entrances, prayer areas, and room flow."),
                        new Recommendation("Bathroom", "Bathroom Renovation", "Modern fittings and tiles improve property perception quickly.", "+14% ROI", "Rs 60K-1.2L", "Prioritize waterproofing, fixtures, and clean finishes.")
                ));
            }

            if (listingRepository.count() == 0) {
                AppUser user = userRepository.findByEmailIgnoreCase("user@betterhome.in").orElseThrow();
                PropertyListing first = new PropertyListing(user, "Demo User", "Pune, Maharashtra", "2BHK", "950 sqft", "Rs 75,000", "5-10 yrs", "Better resale value", null, 72, ListingStatus.ACTIVE, LocalDate.of(2026, 2, 20), null);
                first.setRecommendations(List.of("Modular Kitchen Upgrade", "Facade and Landscaping", "Vastu-Compliant Redesign"));

                PropertyListing second = new PropertyListing(user, "Demo User", "Lucknow, Uttar Pradesh", "3BHK", "1200 sqft", "Rs 40,000", "10-20 yrs", "Damp walls", null, 58, ListingStatus.PENDING, LocalDate.of(2026, 2, 22), null);
                second.setRecommendations(List.of("Bathroom Renovation", "Facade and Landscaping", "Smart Lighting and Automation"));

                listingRepository.saveAll(List.of(first, second));
            }
        };
    }
}
