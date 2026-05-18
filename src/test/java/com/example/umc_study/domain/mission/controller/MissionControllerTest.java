package com.example.umc_study.domain.mission.controller;

import com.example.umc_study.domain.member.entity.Member;
import com.example.umc_study.domain.member.enums.Address;
import com.example.umc_study.domain.member.enums.Gender;
import com.example.umc_study.domain.member.enums.SocialType;
import com.example.umc_study.domain.member.repository.MemberRepository;
import com.example.umc_study.domain.mission.entity.Mission;
import com.example.umc_study.domain.mission.entity.Store;
import com.example.umc_study.domain.mission.entity.mapping.MemberMission;
import com.example.umc_study.domain.mission.enums.MissionStatus;
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
class MissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private MemberMissionRepository memberMissionRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("get my missions filters challenging status and paginates by latest order")
    void getMyChallengingMissions() throws Exception {
        Member member = memberRepository.save(createMember("challenger-1", "challenger1@example.com"));
        Member otherMember = memberRepository.save(createMember("challenger-2", "challenger2@example.com"));
        Store store = storeRepository.save(createStore("Store A"));

        Mission firstMission = missionRepository.save(createMission(store, "Spend over 12000 won", 500));
        Mission secondMission = missionRepository.save(createMission(store, "Order two americanos", 300));
        Mission thirdMission = missionRepository.save(createMission(store, "Spend over 15000 won with dessert", 700));

        MemberMission first = memberMissionRepository.save(createMemberMission(member, firstMission, MissionStatus.CHALLENGING));
        MemberMission second = memberMissionRepository.save(createMemberMission(member, secondMission, MissionStatus.CHALLENGING));
        MemberMission third = memberMissionRepository.save(createMemberMission(member, thirdMission, MissionStatus.CHALLENGING));
        memberMissionRepository.save(createMemberMission(otherMember, thirdMission, MissionStatus.CHALLENGING));

        updateMemberMissionTimestamps(first.getId(), LocalDateTime.of(2026, 5, 1, 10, 0), LocalDateTime.of(2026, 5, 1, 10, 0));
        updateMemberMissionTimestamps(second.getId(), LocalDateTime.of(2026, 5, 2, 10, 0), LocalDateTime.of(2026, 5, 2, 10, 0));
        updateMemberMissionTimestamps(third.getId(), LocalDateTime.of(2026, 5, 3, 10, 0), LocalDateTime.of(2026, 5, 3, 10, 0));

        mockMvc.perform(get("/api/missions/me")
                        .param("memberId", member.getId().toString())
                        .param("status", "CHALLENGING")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result.pagination.listSize").value(2))
                .andExpect(jsonPath("$.result.pagination.pageNumber").value(0))
                .andExpect(jsonPath("$.result.pagination.pageSize").value(2))
                .andExpect(jsonPath("$.result.pagination.totalPages").value(2))
                .andExpect(jsonPath("$.result.pagination.totalElements").value(3))
                .andExpect(jsonPath("$.result.missionList[0].missionSpec").value("Spend over 15000 won with dessert"))
                .andExpect(jsonPath("$.result.missionList[0].rewardPoint").value("700P"))
                .andExpect(jsonPath("$.result.missionList[0].status").value("CHALLENGING"))
                .andExpect(jsonPath("$.result.missionList[1].missionSpec").value("Order two americanos"));
    }

    @Test
    @DisplayName("get my progress missions uses query params and offset pagination")
    void getMyProgressMissionsByOffset() throws Exception {
        Member member = memberRepository.save(createMember("progress-1", "progress1@example.com"));
        Store store = storeRepository.save(createStore("Store Progress"));

        Mission firstMission = missionRepository.save(createMission(store, "Spend over 12000 won", 500));
        Mission secondMission = missionRepository.save(createMission(store, "Order two americanos", 300));
        Mission thirdMission = missionRepository.save(createMission(store, "Spend over 15000 won with dessert", 700));

        MemberMission first = memberMissionRepository.save(createMemberMission(member, firstMission, MissionStatus.CHALLENGING));
        MemberMission second = memberMissionRepository.save(createMemberMission(member, secondMission, MissionStatus.CHALLENGING));
        MemberMission third = memberMissionRepository.save(createMemberMission(member, thirdMission, MissionStatus.CHALLENGING));

        updateMemberMissionTimestamps(first.getId(), LocalDateTime.of(2026, 5, 1, 10, 0), LocalDateTime.of(2026, 5, 1, 10, 0));
        updateMemberMissionTimestamps(second.getId(), LocalDateTime.of(2026, 5, 2, 10, 0), LocalDateTime.of(2026, 5, 2, 10, 0));
        updateMemberMissionTimestamps(third.getId(), LocalDateTime.of(2026, 5, 3, 10, 0), LocalDateTime.of(2026, 5, 3, 10, 0));

        mockMvc.perform(get("/api/missions/me/progress")
                        .param("memberId", member.getId().toString())
                        .param("offset", "1")
                        .param("limit", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result.pagination.offset").value(1))
                .andExpect(jsonPath("$.result.pagination.limit").value(1))
                .andExpect(jsonPath("$.result.pagination.listSize").value(1))
                .andExpect(jsonPath("$.result.pagination.totalElements").value(3))
                .andExpect(jsonPath("$.result.pagination.hasNext").value(true))
                .andExpect(jsonPath("$.result.missionList[0].missionSpec").value("Order two americanos"))
                .andExpect(jsonPath("$.result.missionList[0].status").value("CHALLENGING"));
    }

    @Test
    @DisplayName("get my progress missions returns not found when member does not exist")
    void getMyProgressMissionsFailsWhenMemberMissing() throws Exception {
        mockMvc.perform(get("/api/missions/me/progress")
                        .param("memberId", "999999")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("MEMBER404_1"));
    }

    @Test
    @DisplayName("get my progress missions returns bad request when offset is negative")
    void getMyProgressMissionsFailsWhenOffsetIsNegative() throws Exception {
        Member member = memberRepository.save(createMember("progress-invalid", "progress-invalid@example.com"));

        mockMvc.perform(get("/api/missions/me/progress")
                        .param("memberId", member.getId().toString())
                        .param("offset", "-1")
                        .param("limit", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("COMMON400_1"));
    }

    @Test
    @DisplayName("get my completed missions sorts by latest success time")
    void getMyCompletedMissions() throws Exception {
        Member member = memberRepository.save(createMember("completed-1", "completed1@example.com"));
        Store store = storeRepository.save(createStore("Store B"));

        Mission firstMission = missionRepository.save(createMission(store, "Complete one lunch visit", 400));
        Mission secondMission = missionRepository.save(createMission(store, "Complete one dinner visit", 600));

        MemberMission first = memberMissionRepository.save(createMemberMission(member, firstMission, MissionStatus.COMPLETED));
        MemberMission second = memberMissionRepository.save(createMemberMission(member, secondMission, MissionStatus.COMPLETED));

        updateMemberMissionTimestamps(first.getId(), LocalDateTime.of(2026, 5, 1, 9, 0), LocalDateTime.of(2026, 5, 4, 12, 0));
        updateMemberMissionTimestamps(second.getId(), LocalDateTime.of(2026, 5, 1, 9, 0), LocalDateTime.of(2026, 5, 5, 12, 0));

        mockMvc.perform(get("/api/missions/me")
                        .param("memberId", member.getId().toString())
                        .param("status", "COMPLETED")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.pagination.listSize").value(2))
                .andExpect(jsonPath("$.result.missionList[0].missionSpec").value("Complete one dinner visit"))
                .andExpect(jsonPath("$.result.missionList[0].status").value("COMPLETED"))
                .andExpect(jsonPath("$.result.missionList[1].missionSpec").value("Complete one lunch visit"));
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
                .email(email)
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

    private Store createStore(String name) {
        return Store.builder()
                .name(name)
                .address("Seoul")
                .score(4.3f)
                .build();
    }

    private Mission createMission(Store store, String missionSpec, Integer reward) {
        return Mission.builder()
                .reward(reward)
                .deadline(LocalDate.of(2026, 12, 31))
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
