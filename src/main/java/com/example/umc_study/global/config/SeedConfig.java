package com.example.umc_study.global.config;

import com.example.umc_study.domain.member.entity.Member;
import com.example.umc_study.domain.member.enums.Address;
import com.example.umc_study.domain.member.enums.Gender;
import com.example.umc_study.domain.member.enums.SocialType;
import com.example.umc_study.domain.member.repository.MemberRepository;
import com.example.umc_study.domain.mission.entity.Location;
import com.example.umc_study.domain.mission.entity.Mission;
import com.example.umc_study.domain.mission.entity.Store;
import com.example.umc_study.domain.mission.entity.mapping.MemberMission;
import com.example.umc_study.domain.mission.enums.MissionStatus;
import com.example.umc_study.domain.mission.repository.LocationRepository;
import com.example.umc_study.domain.mission.repository.MemberMissionRepository;
import com.example.umc_study.domain.mission.repository.MissionRepository;
import com.example.umc_study.domain.mission.repository.StoreRepository;
import com.example.umc_study.domain.review.entity.Reply;
import com.example.umc_study.domain.review.entity.Review;
import com.example.umc_study.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;

@Slf4j
@Component
@Profile("local")
@RequiredArgsConstructor
public class SeedConfig implements ApplicationRunner {

    private static final String MEMBER_ONE_SOCIAL_UID = "seed-member-1";
    private static final String MEMBER_TWO_SOCIAL_UID = "seed-member-2";

    private final MemberRepository memberRepository;
    private final LocationRepository locationRepository;
    private final StoreRepository storeRepository;
    private final MissionRepository missionRepository;
    private final MemberMissionRepository memberMissionRepository;
    private final ReviewRepository reviewRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (memberRepository.findBySocialUid(MEMBER_ONE_SOCIAL_UID).isPresent()) {
            logExistingSeedSummary();
            printConsoleSummary("REUSE");
            return;
        }

        seedLocalData();
        printConsoleSummary("CREATED");
    }

    private void seedLocalData() {
        Location gangnam = locationRepository.save(
                Location.builder()
                        .name("Gangnam")
                        .build()
        );

        Location hongdae = locationRepository.save(
                Location.builder()
                        .name("Hongdae")
                        .build()
        );

        Store pastaStore = storeRepository.save(
                Store.builder()
                        .name("UMC Pasta")
                        .address("Seoul Gangnam-gu")
                        .score(4.6f)
                        .category("Italian")
                        .location(gangnam)
                        .build()
        );

        Store chinaStore = storeRepository.save(
                Store.builder()
                        .name("UMC China")
                        .address("Seoul Gangnam-gu")
                        .score(4.4f)
                        .category("Chinese")
                        .location(gangnam)
                        .build()
        );

        Store burgerStore = storeRepository.save(
                Store.builder()
                        .name("UMC Burger")
                        .address("Seoul Mapo-gu")
                        .score(4.2f)
                        .category("Fast Food")
                        .location(hongdae)
                        .build()
        );

        Member memberOne = memberRepository.save(createMember(
                "swagger-user-1",
                "swagger1@example.com",
                "01012345678",
                1200,
                MEMBER_ONE_SOCIAL_UID
        ));

        Member memberTwo = memberRepository.save(createMember(
                "swagger-user-2",
                "swagger2@example.com",
                "01087654321",
                450,
                MEMBER_TWO_SOCIAL_UID
        ));

        Mission pastaMission = missionRepository.save(createMission(
                pastaStore,
                "Spend over 10,000 won",
                500,
                LocalDate.now().plusDays(10)
        ));

        Mission lunchMission = missionRepository.save(createMission(
                chinaStore,
                "Order a lunch set",
                300,
                LocalDate.now().plusDays(7)
        ));

        Mission dinnerMission = missionRepository.save(createMission(
                pastaStore,
                "Spend over 15,000 won",
                700,
                LocalDate.now().plusDays(14)
        ));

        Mission burgerMission = missionRepository.save(createMission(
                burgerStore,
                "Order a burger set",
                400,
                LocalDate.now().plusDays(5)
        ));

        Mission chinaDinnerMission = missionRepository.save(createMission(
                chinaStore,
                "Order a dinner menu",
                600,
                LocalDate.now().plusDays(12)
        ));

        memberMissionRepository.save(createMemberMission(memberOne, pastaMission, MissionStatus.CHALLENGING));
        memberMissionRepository.save(createMemberMission(memberOne, lunchMission, MissionStatus.CHALLENGING));
        memberMissionRepository.save(createMemberMission(memberOne, burgerMission, MissionStatus.COMPLETED));
        memberMissionRepository.save(createMemberMission(memberTwo, chinaDinnerMission, MissionStatus.CHALLENGING));

        reviewRepository.save(createReview(
                memberOne,
                pastaStore,
                "Fresh pasta",
                4.5f,
                "Fresh ingredients and a quick service.",
                ""
        ));

        reviewRepository.save(createReview(
                memberOne,
                chinaStore,
                "Great lunch set",
                5.0f,
                "Good value for money and the portions were generous.",
                ""
        ));

        reviewRepository.save(createReview(
                memberTwo,
                burgerStore,
                "Solid burger",
                4.0f,
                "The burger was juicy and the fries were crispy.",
                ""
        ));

        log.info("[Seed] Local sample data created.");
        logSeedSummary(memberOne, memberTwo, gangnam, hongdae, pastaStore, chinaStore, burgerStore);
        log.info("[Seed] Mission ids: pastaMission={}, lunchMission={}, dinnerMission={}, burgerMission={}, chinaDinnerMission={}",
                pastaMission.getId(),
                lunchMission.getId(),
                dinnerMission.getId(),
                burgerMission.getId(),
                chinaDinnerMission.getId()
        );
    }

    private Member createMember(
            String name,
            String email,
            String phoneNumber,
            int point,
            String socialUid
    ) {
        return Member.builder()
                .name(name)
                .email(email)
                .phoneNumber(phoneNumber)
                .profileUrl("https://example.com/profiles/" + socialUid + ".png")
                .point(point)
                .gender(Gender.FEMALE)
                .birth(LocalDate.of(2000, 1, 1))
                .address(Address.values()[0])
                .detailAddress("101-1001")
                .socialUid(socialUid)
                .socialType(SocialType.KAKAO)
                .build();
    }

    private Mission createMission(Store store, String missionSpec, int reward, LocalDate deadline) {
        return Mission.builder()
                .reward(reward)
                .deadline(deadline)
                .missionSpec(missionSpec)
                .store(store)
                .build();
    }

    private MemberMission createMemberMission(Member member, Mission mission, MissionStatus status) {
        return MemberMission.builder()
                .member(member)
                .mission(mission)
                .status(status)
                .build();
    }

    private Review createReview(
            Member member,
            Store store,
            String title,
            float score,
            String body,
            String replyBody
    ) {
        Reply reply = Reply.builder()
                .body(replyBody)
                .build();

        Review review = Review.builder()
                .title(title)
                .score(score)
                .body(body)
                .member(member)
                .store(store)
                .build();

        review.assignReply(reply);
        return review;
    }

    private void logExistingSeedSummary() {
        Member memberOne = memberRepository.findBySocialUid(MEMBER_ONE_SOCIAL_UID).orElse(null);
        Member memberTwo = memberRepository.findBySocialUid(MEMBER_TWO_SOCIAL_UID).orElse(null);

        log.info("[Seed] Local sample data already exists.");
        if (memberOne != null && memberTwo != null) {
            log.info("[Seed] Reuse member ids: memberOne={}, memberTwo={}", memberOne.getId(), memberTwo.getId());
        }

        storeRepository.findAll().stream()
                .filter(store -> store.getName().startsWith("UMC "))
                .forEach(store -> log.info("[Seed] Store {} -> id={}", store.getName(), store.getId()));

        locationRepository.findAll().stream()
                .filter(location -> location.getName().equals("Gangnam") || location.getName().equals("Hongdae"))
                .forEach(location -> log.info("[Seed] Region {} -> id={}", location.getName(), location.getId()));
    }

    private void printConsoleSummary(String mode) {
        Member memberOne = memberRepository.findBySocialUid(MEMBER_ONE_SOCIAL_UID).orElse(null);
        Member memberTwo = memberRepository.findBySocialUid(MEMBER_TWO_SOCIAL_UID).orElse(null);

        System.out.println();
        System.out.println("==================================================");
        System.out.println(" LOCAL SWAGGER SEED " + mode);
        System.out.println("==================================================");

        if (memberOne != null) {
            System.out.println("memberOne (swagger-user-1) id = " + memberOne.getId());
        }
        if (memberTwo != null) {
            System.out.println("memberTwo (swagger-user-2) id = " + memberTwo.getId());
        }

        locationRepository.findAll().stream()
                .filter(location -> location.getName().equals("Gangnam") || location.getName().equals("Hongdae"))
                .sorted(Comparator.comparing(Location::getId))
                .forEach(location -> System.out.println("region " + location.getName() + " id = " + location.getId()));

        storeRepository.findAll().stream()
                .filter(store -> store.getName().startsWith("UMC "))
                .sorted(Comparator.comparing(Store::getId))
                .forEach(store -> System.out.println("store " + store.getName() + " id = " + store.getId()));

        missionRepository.findAll().stream()
                .filter(mission -> mission.getStore() != null && mission.getStore().getName().startsWith("UMC "))
                .sorted(Comparator.comparing(Mission::getId))
                .forEach(mission -> System.out.println("mission [" + mission.getMissionSpec() + "] id = " + mission.getId()));

        System.out.println("--------------------------------------------------");
        System.out.println("Swagger quick start");
        System.out.println("My Page: POST /api/v1/users/me -> memberOne id");
        System.out.println("Create Review: POST /api/stores/{storeId}/reviews -> memberOne id + pasta/china store id");
        System.out.println("My Reviews: POST /api/reviews/me -> memberOne id");
        System.out.println("Mission Progress: GET /api/missions/me/progress -> memberOne id + offset + limit");
        System.out.println("Mission Home: GET /api/missions/home -> memberOne id + Gangnam region id");
        System.out.println("==================================================");
        System.out.println();
    }

    private void logSeedSummary(
            Member memberOne,
            Member memberTwo,
            Location gangnam,
            Location hongdae,
            Store pastaStore,
            Store chinaStore,
            Store burgerStore
    ) {
        log.info("[Seed] Member ids: memberOne={}, memberTwo={}", memberOne.getId(), memberTwo.getId());
        log.info("[Seed] Region ids: gangnam={}, hongdae={}", gangnam.getId(), hongdae.getId());
        log.info("[Seed] Store ids: pastaStore={}, chinaStore={}, burgerStore={}",
                pastaStore.getId(),
                chinaStore.getId(),
                burgerStore.getId()
        );
        log.info("[Seed] Swagger quick start: myPage memberId={}, review storeId={}, home regionId={}",
                memberOne.getId(),
                pastaStore.getId(),
                gangnam.getId()
        );
    }
}
