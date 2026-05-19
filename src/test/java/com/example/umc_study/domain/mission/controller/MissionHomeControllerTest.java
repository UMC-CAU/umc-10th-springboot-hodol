package com.example.umc_study.domain.mission.controller;

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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class MissionHomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private MemberMissionRepository memberMissionRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("home missions filter by region and exclude already joined missions")
    void getHomeMissionsByLatest() throws Exception {
        Member member = memberRepository.save(createMember("home-user-1", "home1@example.com"));
        Location targetRegion = locationRepository.save(createLocation("Gangnam"));
        Location otherRegion = locationRepository.save(createLocation("Hongdae"));

        Store pastaStore = storeRepository.save(createStore("UMC Pasta", "Western", targetRegion));
        Store chineseStore = storeRepository.save(createStore("UMC China", "Chinese", targetRegion));
        Store otherRegionStore = storeRepository.save(createStore("UMC Burger", "FastFood", otherRegion));

        Mission joinedMission = missionRepository.save(createMission(pastaStore, "Spend over 10000 won", 500, LocalDate.of(2026, 5, 20)));
        Mission latestMission = missionRepository.save(createMission(chineseStore, "Order a lunch set", 300, LocalDate.of(2026, 5, 18)));
        Mission olderMission = missionRepository.save(createMission(pastaStore, "Order pasta and drink", 700, LocalDate.of(2026, 5, 25)));
        missionRepository.save(createMission(otherRegionStore, "Region should not appear", 200, LocalDate.of(2026, 5, 17)));

        MemberMission joined = memberMissionRepository.save(createMemberMission(member, joinedMission, MissionStatus.CHALLENGING));
        updateMemberMissionTimestamps(joined.getId(), LocalDateTime.of(2026, 5, 1, 10, 0), LocalDateTime.of(2026, 5, 1, 10, 0));
        updateMissionCreatedAt(latestMission.getId(), LocalDateTime.of(2026, 5, 3, 10, 0));
        updateMissionCreatedAt(olderMission.getId(), LocalDateTime.of(2026, 5, 2, 10, 0));

        mockMvc.perform(get("/api/missions/home")
                        .param("memberId", member.getId().toString())
                        .param("regionId", targetRegion.getId().toString())
                        .param("sortType", "LATEST")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result.progress.currentMissionCount").value(1))
                .andExpect(jsonPath("$.result.progress.targetMissionCount").value(10))
                .andExpect(jsonPath("$.result.progress.targetRewardPoint").value("1000P"))
                .andExpect(jsonPath("$.result.pagination.totalElements").value(2))
                .andExpect(jsonPath("$.result.missionList[0].storeName").value("UMC China"))
                .andExpect(jsonPath("$.result.missionList[0].category").value("Chinese"))
                .andExpect(jsonPath("$.result.missionList[1].storeName").value("UMC Pasta"));
    }

    @Test
    @DisplayName("home missions can be sorted by nearest deadline")
    void getHomeMissionsByDeadline() throws Exception {
        Member member = memberRepository.save(createMember("home-user-2", "home2@example.com"));
        Location targetRegion = locationRepository.save(createLocation("Jamsil"));

        Store store = storeRepository.save(createStore("UMC Curry", "Indian", targetRegion));
        Mission farMission = missionRepository.save(createMission(store, "Visit next month", 400, LocalDate.now().plusDays(10)));
        Mission nearMission = missionRepository.save(createMission(store, "Visit this week", 600, LocalDate.now().plusDays(2)));

        updateMissionCreatedAt(farMission.getId(), LocalDateTime.of(2026, 5, 1, 9, 0));
        updateMissionCreatedAt(nearMission.getId(), LocalDateTime.of(2026, 5, 2, 9, 0));

        mockMvc.perform(get("/api/missions/home")
                        .param("memberId", member.getId().toString())
                        .param("regionId", targetRegion.getId().toString())
                        .param("sortType", "DEADLINE")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.missionList[0].missionSpec").value("Visit this week"))
                .andExpect(jsonPath("$.result.missionList[0].rewardPoint").value("600P"))
                .andExpect(jsonPath("$.result.missionList[0].category").value("Indian"))
                .andExpect(jsonPath("$.result.missionList[1].missionSpec").value("Visit next month"));
    }

    private void updateMissionCreatedAt(Long missionId, LocalDateTime createdAt) {
        jdbcTemplate.update(
                "update mission set created_at = ?, updated_at = ? where id = ?",
                Timestamp.valueOf(createdAt),
                Timestamp.valueOf(createdAt),
                missionId
        );
    }

    private void updateMemberMissionTimestamps(Long memberMissionId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        jdbcTemplate.update(
                "update member_mission set created_at = ?, updated_at = ? where id = ?",
                Timestamp.valueOf(createdAt),
                Timestamp.valueOf(updatedAt),
                memberMissionId
        );
    }

    private Member createMember(String name, String email) {
        return Member.builder()
                .name(name)
                .nickname(name)
                .email(email)
                .password("encoded-password")
                .phoneNumber("01012345678")
                .profileUrl("https://example.com/profile.png")
                .point(0)
                .gender(Gender.FEMALE)
                .birth(LocalDate.of(2000, 1, 1))
                .address(Address.values()[0])
                .detailAddress("101-1001")
                .socialUid(name + "-uid")
                .socialType(SocialType.KAKAO)
                .build();
    }

    private Location createLocation(String name) {
        return Location.builder()
                .name(name)
                .build();
    }

    private Store createStore(String name, String category, Location location) {
        return Store.builder()
                .name(name)
                .address("Seoul")
                .score(4.3f)
                .category(category)
                .location(location)
                .build();
    }

    private Mission createMission(Store store, String missionSpec, Integer reward, LocalDate deadline) {
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
}
