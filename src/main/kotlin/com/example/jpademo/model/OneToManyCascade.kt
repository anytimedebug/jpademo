package com.example.jpademo.model

import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.*

@Entity
data class ParentFifth(

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int,

        @Column
        val pName: String,

        @Column
        val pContent: String,

        @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY, mappedBy = "parent")
        val childrenAll: MutableList<ChildFifth> = mutableListOf(),

        @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = false, fetch = FetchType.LAZY, mappedBy = "parent")
        val childrenAllRemovalFalse: MutableList<ChildFifth> = mutableListOf(),

        @OneToMany(cascade = [CascadeType.PERSIST], orphanRemoval = true, fetch = FetchType.LAZY, mappedBy = "parent")
        val childrenPersist: MutableList<ChildFifth> = mutableListOf(),

        @OneToMany(cascade = [CascadeType.PERSIST], orphanRemoval = false, fetch = FetchType.LAZY, mappedBy = "parent")
        val childrenPersistRemovalFalse: MutableList<ChildFifth> = mutableListOf(),

        @OneToMany(cascade = [CascadeType.MERGE], orphanRemoval = true, fetch = FetchType.LAZY, mappedBy = "parent")
        val childrenMerge: MutableList<ChildSixth> = mutableListOf(),

        @OneToMany(cascade = [CascadeType.MERGE], orphanRemoval = false, fetch = FetchType.LAZY, mappedBy = "parent")
        val childrenMergeRemovalFalse: MutableList<ChildSixth> = mutableListOf(),

        @OneToMany(cascade = [CascadeType.REMOVE], orphanRemoval = true, fetch = FetchType.LAZY, mappedBy = "parent")
        val childrenRemove: MutableList<ChildSeventh> = mutableListOf(),

        @OneToMany(cascade = [CascadeType.REMOVE], orphanRemoval = false, fetch = FetchType.LAZY, mappedBy = "parent")
        val childrenRemoveRemovalFalse: MutableList<ChildSeventh> = mutableListOf()

)

@Entity
data class ChildFifth(

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int,

        @Column
        val cName: String,

        @Column
        val cContent: String,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "p_id") // 설정 안하면 parent_id로 생성됨
        val parent: ParentFifth? = null

)

@Entity
data class ChildSixth(

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int,

        @Column
        val cName: String,

        @Column
        val cContent: String,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "p_id") // 설정 안하면 parent_id로 생성됨
        val parent: ParentFifth? = null

)

@Entity
data class ChildSeventh(

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int,

        @Column
        val cName: String,

        @Column
        val cContent: String,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "p_id") // 설정 안하면 parent_id로 생성됨
        val parent: ParentFifth? = null

)

interface ParentFifthRepository : JpaRepository<ParentFifth, Int>