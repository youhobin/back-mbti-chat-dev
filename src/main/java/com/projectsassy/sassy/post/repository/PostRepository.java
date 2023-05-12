package com.projectsassy.sassy.post.repository;

import com.projectsassy.sassy.post.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long> {


    @Query(value = "select p from Post p " +
            "join fetch p.user u " +
            "order by p.count desc, p.id desc",
            countQuery = "select count(p) from Post p")
    Page<Post> findAllByViewed(Pageable pageable);

    @Query(value = "select p from Post p " +
            "join fetch p.user u " +
            "order by p.createdAt desc",
            countQuery = "select count(p) from Post p")
    Page<Post> findAllByNewest(Pageable pageable);
}
