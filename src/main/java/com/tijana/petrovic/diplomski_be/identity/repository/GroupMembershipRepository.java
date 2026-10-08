package com.tijana.petrovic.diplomski_be.identity.repository;

import com.tijana.petrovic.diplomski_be.identity.entity.GroupMembership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GroupMembershipRepository extends JpaRepository<GroupMembership, UUID> {

    boolean existsByUserIdAndGroupId(UUID userId, UUID groupId);

    List<GroupMembership> findByUserId(UUID userId);

    List<GroupMembership> findByGroupId(UUID groupId);

    void deleteByUserIdAndGroupId(UUID userId, UUID groupId);
}
