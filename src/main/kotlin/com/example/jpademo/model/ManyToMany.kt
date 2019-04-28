package com.example.jpademo.model

import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.*

@Entity
data class ParentFourth(

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int,

        @Column
        val pName: String,

        @Column
        val pContent: String,

        @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        @JoinTable(
                name = "parent_child", // 명시하지 않으면 parent_fourth_children
                joinColumns = [JoinColumn(name = "parent_id")], // 명시하지 않으면 parent_fourth_id
                inverseJoinColumns = [JoinColumn(name = "child_id")] // 명시하지 않으면 children_id
        )
        val children: MutableList<ChildFourth>

)

@Entity
data class ChildFourth(

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int,

        @Column
        val cName: String,

        @Column
        val cContent: String

)

interface ParentFourthRepository : JpaRepository<ParentFourth, Int>