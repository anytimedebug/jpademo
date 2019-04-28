package com.example.jpademo

import com.example.jpademo.model.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.junit4.SpringRunner
import java.util.*


@Suppress("NonAsciiCharacters", "SqlNoDataSourceInspection", "SqlResolve")
@RunWith(SpringRunner::class)
@DataJpaTest
class CascadeTest {

    @Autowired
    private lateinit var parentFifthRepository: ParentFifthRepository

    @Autowired
    private lateinit var em: TestEntityManager

    private fun creatParent() {
        em.entityManager.createNativeQuery(
                "insert into parent_fifth (id, p_content, p_name) values (null, '${UUID.randomUUID()}', '${UUID.randomUUID()}')")
                .executeUpdate()
    }

    private fun createChild(parentId: Int, tbName: String = "child_fifth") {
        em.entityManager.createNativeQuery(
                "insert into $tbName (id, c_content, c_name, p_id) values (null, '${UUID.randomUUID()}', '${UUID.randomUUID()}', $parentId)")
                .executeUpdate()
    }

    private fun selectList(tbName: String = "child_fifth") =
            em.entityManager.createNativeQuery("select * from $tbName").resultList

    @Test
    fun `cascade가 all이고 orphanRemoval이 true인 children을 저장한다`() {
        // arrange
        val child1 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child2 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child3 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val children = mutableListOf(child1, child2, child3)
        val parent = ParentFifth(id = 0,
                pName = UUID.randomUUID().toString(),
                pContent = UUID.randomUUID().toString(),
                childrenAll = children)
        parentFifthRepository.save(parent)

        // actual
        val actual = selectList().size

        // assert
        assertThat(actual, `is`(children.size))
    }

    @Test
    fun `cascade가 all이고 orphanRemoval이 true인 children을 하나 추가한다`() {
        // arrange
        val child1 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child2 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child3 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val children = mutableListOf(child1, child2, child3)
        val parent = ParentFifth(id = 0,
                pName = UUID.randomUUID().toString(),
                pContent = UUID.randomUUID().toString(),
                childrenAll = children)
        parentFifthRepository.save(parent)

        val savedParent = parentFifthRepository.findAll().last()
        val updatingParent = savedParent.copy()
        val child4 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        updatingParent.childrenAll.add(child4)
        parentFifthRepository.save(updatingParent)

        // actual
        val actual = selectList().size

        // assert
        assertThat(actual, `is`(updatingParent.childrenAll.size))
    }

    @Test
    fun `cascade가 all이고 orphanRemoval이 true인 children을 하나 제거한다`() {
        // arrange
        val child1 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child2 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child3 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val children = mutableListOf(child1, child2, child3)
        val parent = ParentFifth(id = 0,
                pName = UUID.randomUUID().toString(),
                pContent = UUID.randomUUID().toString(),
                childrenAll = children)
        parentFifthRepository.save(parent)

        val savedParent = parentFifthRepository.findAll().last()
        val updatingParent = savedParent.copy()
        updatingParent.childrenAll.removeAt(savedParent.childrenAll.size - 1)
        parentFifthRepository.save(updatingParent)

        // actual
        val actual = selectList().size

        // assert
        assertThat(actual, `is`(updatingParent.childrenAll.size))
    }

    @Test
    fun `cascade가 all이고 orphanRemoval이 true인 children을 수정한다`() {
        // arrange
        val child = ChildFifth(0, "", "")
        val children = mutableListOf(child)
        val parent = ParentFifth(id = 0,
                pName = UUID.randomUUID().toString(),
                pContent = UUID.randomUUID().toString(),
                childrenAll = children)
        parentFifthRepository.save(parent)

        val savedParent = parentFifthRepository.findAll().last()
        val cName = UUID.randomUUID().toString()
        val cContent = UUID.randomUUID().toString()
        val updatingChildren = savedParent.childrenAll
                .map { it.copy(cName = cName, cContent = cContent) }
                .toMutableList()
        val updatingParent = savedParent.copy(childrenAll = updatingChildren)
        parentFifthRepository.saveAndFlush(updatingParent)

        // actual
        val actual = parentFifthRepository.findAll().last().childrenAll.last()

        // assert
        assertThat(actual.cName, `is`(cName))
        assertThat(actual.cContent, `is`(cContent))
    }

    @Test
    fun `cascade가 all이고 orphanRemoval이 true인 children을 삭제한다`() {
        // arrange
        val child1 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child2 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child3 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val children = mutableListOf(child1, child2, child3)
        val parent = ParentFifth(id = 0,
                pName = UUID.randomUUID().toString(),
                pContent = UUID.randomUUID().toString(),
                childrenAll = children)
        parentFifthRepository.save(parent)

        parentFifthRepository.delete(parent)

        // actual
        val actual = selectList()

        // assert
        assertTrue(actual.isEmpty())
    }

    @Test
    fun `cascade가 all이고 orphanRemoval이 false인 children을 저장한다`() {
        // arrange
        val child1 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child2 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child3 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val children = mutableListOf(child1, child2, child3)
        val parent = ParentFifth(id = 0,
                pName = UUID.randomUUID().toString(),
                pContent = UUID.randomUUID().toString(),
                childrenAllRemovalFalse = children)
        parentFifthRepository.save(parent)

        // actual
        val actual = selectList().size

        // assert
        assertThat(actual, `is`(children.size))
    }

    @Test
    fun `cascade가 all이고 orphanRemoval이 false인 children을 하나 추가한다`() {
        // arrange
        val child1 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child2 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child3 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val children = mutableListOf(child1, child2, child3)
        val parent = ParentFifth(id = 0,
                pName = UUID.randomUUID().toString(),
                pContent = UUID.randomUUID().toString(),
                childrenAllRemovalFalse = children)
        parentFifthRepository.save(parent)

        val savedParent = parentFifthRepository.findAll().last()
        val updatingParent = savedParent.copy()
        val child4 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        updatingParent.childrenAllRemovalFalse.add(child4)
        parentFifthRepository.save(updatingParent)

        // actual
        val actual = selectList().size

        // assert
        assertThat(actual, `is`(updatingParent.childrenAllRemovalFalse.size))
    }

    @Test
    fun `cascade가 all이고 orphanRemoval이 false인 children을 하나 제거하지만 제거되지 않는다`() {
        // arrange
        val child1 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child2 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child3 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val children = mutableListOf(child1, child2, child3)
        val parent = ParentFifth(id = 0,
                pName = UUID.randomUUID().toString(),
                pContent = UUID.randomUUID().toString(),
                childrenAllRemovalFalse = children)
        parentFifthRepository.save(parent)

        val savedParent = parentFifthRepository.findAll().last()
        val updatingParent = savedParent.copy()
        updatingParent.childrenAllRemovalFalse.removeAt(savedParent.childrenAllRemovalFalse.size - 1)
        parentFifthRepository.save(updatingParent)

        // actual
        val actual = selectList().size

        // assert
        assertThat(actual, not(updatingParent.childrenAllRemovalFalse.size))
    }

    @Test
    fun `cascade가 all이고 orphanRemoval이 false인 children을 수정한다`() {
        // arrange
        val child = ChildFifth(0, "", "")
        val children = mutableListOf(child)
        val parent = ParentFifth(id = 0,
                pName = UUID.randomUUID().toString(),
                pContent = UUID.randomUUID().toString(),
                childrenAllRemovalFalse = children)
        parentFifthRepository.save(parent)

        val savedParent = parentFifthRepository.findAll().last()
        val cName = UUID.randomUUID().toString()
        val cContent = UUID.randomUUID().toString()
        val updatingChildren = savedParent.childrenAllRemovalFalse
                .map { it.copy(cName = cName, cContent = cContent) }
                .toMutableList()
        val updatingParent = savedParent.copy(childrenAllRemovalFalse = updatingChildren)
        parentFifthRepository.saveAndFlush(updatingParent)

        // actual
        val actual = parentFifthRepository.findAll().last().childrenAllRemovalFalse.last()

        // assert
        assertThat(actual.cName, `is`(cName))
        assertThat(actual.cContent, `is`(cContent))
    }

    @Test
    fun `cascade가 all이고 orphanRemoval이 false인 children을 삭제한다`() {
        // arrange
        val child1 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child2 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child3 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val children = mutableListOf(child1, child2, child3)
        val parent = ParentFifth(id = 0,
                pName = UUID.randomUUID().toString(),
                pContent = UUID.randomUUID().toString(),
                childrenAllRemovalFalse = children)
        parentFifthRepository.save(parent)

        parentFifthRepository.delete(parent)

        // actual
        val actual = selectList()

        // assert
        assertTrue(actual.isEmpty())
    }

    @Test
    fun `cascade가 persist이고 orphanRemoval이 true인 children을 저장한다`() {
        // arrange
        val child1 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child2 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child3 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val children = mutableListOf(child1, child2, child3)
        val parent = ParentFifth(id = 0,
                pName = UUID.randomUUID().toString(),
                pContent = UUID.randomUUID().toString(),
                childrenPersist = children)
        parentFifthRepository.save(parent)

        // actual
        val actual = selectList().size

        // assert
        assertThat(actual, `is`(children.size))
    }

    @Test
    fun `cascade가 persist이고 orphanRemoval이 true인 children을 하나 추가한다`() {
        // arrange
        val child1 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child2 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child3 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val children = mutableListOf(child1, child2, child3)
        val parent = ParentFifth(id = 0,
                pName = UUID.randomUUID().toString(),
                pContent = UUID.randomUUID().toString(),
                childrenPersist = children)
        parentFifthRepository.save(parent)

        val savedParent = parentFifthRepository.findAll().last()
        val updatingParent = savedParent.copy()
        val child4 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        updatingParent.childrenPersist.add(child4)
        parentFifthRepository.save(updatingParent)

        // actual
        val actual = selectList().size

        // assert
        assertThat(actual, `is`(updatingParent.childrenPersist.size))
    }

    @Test
    fun `cascade가 persist이고 orphanRemoval이 true인 children을 하나 제거한다`() {
        // arrange
        val child1 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child2 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child3 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val children = mutableListOf(child1, child2, child3)
        val parent = ParentFifth(id = 0,
                pName = UUID.randomUUID().toString(),
                pContent = UUID.randomUUID().toString(),
                childrenPersist = children)
        parentFifthRepository.save(parent)

        val savedParent = parentFifthRepository.findAll().last()
        val updatingParent = savedParent.copy()
        updatingParent.childrenPersist.removeAt(savedParent.childrenPersist.size - 1)
        parentFifthRepository.save(updatingParent)

        // actual
        val actual = selectList().size

        // assert
        assertThat(actual, `is`(updatingParent.childrenPersist.size))
    }

    @Test
    fun `cascade가 persist이고 orphanRemoval이 true인 children을 수정하지만 수정되지 않고 삭제된다`() {
        // arrange
        val child = ChildFifth(0, "", "")
        val children = mutableListOf(child)
        val parent = ParentFifth(id = 0,
                pName = UUID.randomUUID().toString(),
                pContent = UUID.randomUUID().toString(),
                childrenPersist = children)
        parentFifthRepository.save(parent)

        val savedParent = parentFifthRepository.findAll().last()
        val cName = UUID.randomUUID().toString()
        val cContent = UUID.randomUUID().toString()
        val updatingChildren = savedParent.childrenAll
                .map { it.copy(cName = cName, cContent = cContent) }
                .toMutableList()
        val updatingParent = savedParent.copy(childrenPersist = updatingChildren)
        parentFifthRepository.saveAndFlush(updatingParent)

        // actual
        val actual = parentFifthRepository.findAll().last().childrenPersist

        val actual2 = selectList()

        // assert
        assertTrue(actual.isEmpty())
        assertTrue(actual2.isEmpty())
    }

    @Test
    fun `cascade가 persist이고 orphanRemoval이 true인 children을 삭제한다`() {
        // arrange
        val child1 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child2 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child3 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val children = mutableListOf(child1, child2, child3)
        val parent = ParentFifth(id = 0,
                pName = UUID.randomUUID().toString(),
                pContent = UUID.randomUUID().toString(),
                childrenPersist = children)
        parentFifthRepository.save(parent)

        parentFifthRepository.delete(parent)

        // actual
        val actual = selectList()

        // assert
        assertTrue(actual.isEmpty())
    }

    @Test
    fun `cascade가 persist이고 orphanRemoval이 false인 children을 저장한다`() {
        // arrange
        val child1 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child2 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child3 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val children = mutableListOf(child1, child2, child3)
        val parent = ParentFifth(id = 0,
                pName = UUID.randomUUID().toString(),
                pContent = UUID.randomUUID().toString(),
                childrenPersistRemovalFalse = children)
        parentFifthRepository.save(parent)

        // actual
        val actual = selectList().size

        // assert
        assertThat(actual, `is`(children.size))
    }

    @Test
    fun `cascade가 persist이고 orphanRemoval이 false인 children을 하나 추가한다`() {
        // arrange
        val child1 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child2 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child3 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val children = mutableListOf(child1, child2, child3)
        val parent = ParentFifth(id = 0,
                pName = UUID.randomUUID().toString(),
                pContent = UUID.randomUUID().toString(),
                childrenPersistRemovalFalse = children)
        parentFifthRepository.save(parent)

        val savedParent = parentFifthRepository.findAll().last()
        val updatingParent = savedParent.copy()
        val child4 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        updatingParent.childrenPersistRemovalFalse.add(child4)
        parentFifthRepository.save(updatingParent)

        // actual
        val actual = selectList().size

        // assert
        assertThat(actual, `is`(updatingParent.childrenPersistRemovalFalse.size))
    }

    @Test
    fun `cascade가 persist이고 orphanRemoval이 false인 children을 하나 제거하지만 제거되지 않는다`() {
        // arrange
        val child1 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child2 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child3 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val children = mutableListOf(child1, child2, child3)
        val parent = ParentFifth(id = 0,
                pName = UUID.randomUUID().toString(),
                pContent = UUID.randomUUID().toString(),
                childrenPersistRemovalFalse = children)
        parentFifthRepository.save(parent)

        val savedParent = parentFifthRepository.findAll().last()
        val updatingParent = savedParent.copy()
        updatingParent.childrenPersistRemovalFalse.removeAt(savedParent.childrenPersistRemovalFalse.size - 1)
        parentFifthRepository.save(updatingParent)

        // actual
        val actual = selectList().size

        // assert
        assertThat(actual, not(updatingParent.childrenPersistRemovalFalse.size))
    }

    @Test
    fun `cascade가 persist이고 orphanRemoval이 false인 children을 수정하지만 수정되지 않는다`() {
        // arrange
        val child = ChildFifth(0, "", "")
        val children = mutableListOf(child)
        val parent = ParentFifth(id = 0,
                pName = UUID.randomUUID().toString(),
                pContent = UUID.randomUUID().toString(),
                childrenPersistRemovalFalse = children)
        parentFifthRepository.save(parent)

        val savedParent = parentFifthRepository.findAll().last()
        val cName = UUID.randomUUID().toString()
        val cContent = UUID.randomUUID().toString()
        val updatingChildren = savedParent.childrenAll
                .map { it.copy(cName = cName, cContent = cContent) }
                .toMutableList()
        val updatingParent = savedParent.copy(childrenPersistRemovalFalse = updatingChildren)
        parentFifthRepository.saveAndFlush(updatingParent)

        // actual
        val actual = parentFifthRepository.findAll().last().childrenPersistRemovalFalse


        val actual2 = selectList()

        // assert
        assertTrue(actual.isEmpty())
        assertTrue(actual2.isNotEmpty())

    }

    @Test
    fun `cascade가 persist이고 orphanRemoval이 false인 children을 삭제하지만 삭제되지 않는다`() {
        // arrange
        val child1 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child2 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child3 = ChildFifth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val children = mutableListOf(child1, child2, child3)
        val parent = ParentFifth(id = 0,
                pName = UUID.randomUUID().toString(),
                pContent = UUID.randomUUID().toString(),
                childrenPersistRemovalFalse = children)
        parentFifthRepository.save(parent)

        parentFifthRepository.delete(parent)

        // actual
        val actual = selectList()

        // assert
        assertTrue(actual.isNotEmpty())
    }

    @Test
    fun `cascade가 merge이고 orphanRemoval이 true인 children을 저장하지만 저장되지 않는다`() {
        // arrange
        val child1 = ChildSixth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child2 = ChildSixth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child3 = ChildSixth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val children = mutableListOf(child1, child2, child3)
        val parent = ParentFifth(id = 0,
                pName = UUID.randomUUID().toString(),
                pContent = UUID.randomUUID().toString(),
                childrenMerge = children)
        parentFifthRepository.save(parent)

        // actual

        val actual = selectList("child_sixth").size

        // assert
        assertThat(actual, not(children.size))
    }

    @Test
    fun `cascade가 merge이고 orphanRemoval이 true인 children을 하나 추가한다`() {
        // arrange
        creatParent()

        val savedParent = parentFifthRepository.findAll().last()

        createChild(savedParent.id, "child_sixth")
        createChild(savedParent.id, "child_sixth")
        createChild(savedParent.id, "child_sixth")

        val updatingParent = savedParent.copy()
        val child4 = ChildSixth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        updatingParent.childrenMerge.add(child4)
        parentFifthRepository.save(updatingParent)

        // actual

        val actual = selectList("child_sixth").size

        // assert
        assertThat(actual, `is`(4))
    }

    @Test
    fun `cascade가 merge이고 orphanRemoval이 true인 children을 하나 제거하지만 제거되지 않는다`() {
        // arrange
        creatParent()

        val savedParent = parentFifthRepository.findAll().last()

        createChild(savedParent.id, "child_sixth")
        createChild(savedParent.id, "child_sixth")
        createChild(savedParent.id, "child_sixth")

        val savedParent2 = parentFifthRepository.findAll().last()
        val updatingParent = savedParent2.copy()
        updatingParent.childrenMerge.removeAt(savedParent2.childrenMerge.size - 1)
        parentFifthRepository.save(updatingParent)

        // actual
        val actual = selectList("child_sixth").size

        // assert
        assertThat(actual, not(2))
    }

    @Test
    fun `cascade가 merge이고 orphanRemoval이 true인 children을 수정한다`() {
        // arrange
        creatParent()
        val savedParent = parentFifthRepository.findAll().last()

        createChild(savedParent.id, "child_sixth")

        val savedParent2 = parentFifthRepository.findAll().last()

        val cName = UUID.randomUUID().toString()
        val cContent = UUID.randomUUID().toString()
        val updatingChild = savedParent2.childrenMerge.last().copy(cName = cName, cContent = cContent)
        val updatingParent = savedParent2.copy(childrenMerge = mutableListOf(updatingChild))
        parentFifthRepository.saveAndFlush(updatingParent)

        // actual
        val actual = parentFifthRepository.findAll().last().childrenMerge.last()

        // assert
        assertThat(actual.cName, `is`(cName))
        assertThat(actual.cContent, `is`(cContent))
    }

    @Test
    fun `cascade가 merge이고 orphanRemoval이 true인 children을 삭제한다`() {
        // arrange
        creatParent()
        val savedParent = parentFifthRepository.findAll().last()

        createChild(savedParent.id, "child_sixth")
        createChild(savedParent.id, "child_sixth")
        createChild(savedParent.id, "child_sixth")

        parentFifthRepository.delete(savedParent)

        // actual
        val actual = selectList("child_sixth")

        // assert
        assertTrue(actual.isEmpty())
    }

    @Test
    fun `cascade가 merge이고 orphanRemoval이 false인 children을 저장하지만 저장되지 않는다`() {
        // arrange
        val child1 = ChildSixth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child2 = ChildSixth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child3 = ChildSixth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val children = mutableListOf(child1, child2, child3)
        val parent = ParentFifth(id = 0,
                pName = UUID.randomUUID().toString(),
                pContent = UUID.randomUUID().toString(),
                childrenMergeRemovalFalse = children)
        parentFifthRepository.save(parent)

        // actual

        val actual = selectList("child_sixth").size

        // assert
        assertThat(actual, not(children.size))
    }

    @Test
    fun `cascade가 merge이고 orphanRemoval이 false인 children을 하나 추가한다`() {
        // arrange
        creatParent()

        val savedParent = parentFifthRepository.findAll().last()

        createChild(savedParent.id, "child_sixth")
        createChild(savedParent.id, "child_sixth")
        createChild(savedParent.id, "child_sixth")

        val updatingParent = savedParent.copy()
        val child4 = ChildSixth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        updatingParent.childrenMergeRemovalFalse.add(child4)
        parentFifthRepository.save(updatingParent)

        // actual

        val actual = selectList("child_sixth").size

        // assert
        assertThat(actual, `is`(4))
    }

    @Test
    fun `cascade가 merge이고 orphanRemoval이 false인 children을 하나 제거한다`() {
        // arrange
        creatParent()

        val savedParent = parentFifthRepository.findAll().last()

        createChild(savedParent.id, "child_sixth")
        createChild(savedParent.id, "child_sixth")
        createChild(savedParent.id, "child_sixth")

        val updatingParent = savedParent.copy()
        updatingParent.childrenMergeRemovalFalse.removeAt(savedParent.childrenMergeRemovalFalse.size - 1)
        parentFifthRepository.save(updatingParent)

        // actual
        val actual = selectList("child_sixth").size

        // assert
        assertThat(actual, not(2))
    }

    @Test(expected = StackOverflowError::class)
    fun `cascade가 merge이고 orphanRemoval이 false인 children을 수정하지만 오류가 발생한다`() {
        // arrange
        creatParent()
        val savedParent = parentFifthRepository.findAll().last()

        createChild(savedParent.id, "child_sixth")

        val savedParent2 = parentFifthRepository.findAll().last()

        val cName = UUID.randomUUID().toString()
        val cContent = UUID.randomUUID().toString()
        val updatingChild = savedParent2.childrenMergeRemovalFalse.last().copy(cName = cName, cContent = cContent)
        val updatingParent = savedParent2.copy(childrenMerge = mutableListOf(updatingChild))
        parentFifthRepository.saveAndFlush(updatingParent)
    }

    @Test
    fun `cascade가 merge이고 orphanRemoval이 false인 children을 삭제한다`() {
        // arrange
        creatParent()
        val savedParent = parentFifthRepository.findAll().last()

        createChild(savedParent.id, "child_sixth")
        createChild(savedParent.id, "child_sixth")
        createChild(savedParent.id, "child_sixth")

        parentFifthRepository.delete(savedParent)

        // actual
        val actual = selectList("child_sixth")

        // assert
        assertTrue(actual.isEmpty())
    }

    @Test
    fun `cascade가 remove이고 orphanRemoval이 true인 children을 저장하지만 저장되지 않는다`() {
        // arrange
        val child1 = ChildSeventh(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child2 = ChildSeventh(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child3 = ChildSeventh(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val children = mutableListOf(child1, child2, child3)
        val parent = ParentFifth(id = 0,
                pName = UUID.randomUUID().toString(),
                pContent = UUID.randomUUID().toString(),
                childrenRemove = children)
        parentFifthRepository.save(parent)

        // actual
        val actual = selectList("child_seventh").size

        // assert
        assertThat(actual, not(children.size))
    }

    @Test
    fun `cascade가 remove이고 orphanRemoval이 true인 children을 하나 추가하지만 추가되지 않는다`() {
        // arrange
        creatParent()

        val savedParent = parentFifthRepository.findAll().last()

        createChild(savedParent.id, "child_seventh")
        createChild(savedParent.id, "child_seventh")
        createChild(savedParent.id, "child_seventh")

        val updatingParent = savedParent.copy()
        val child4 = ChildSeventh(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        updatingParent.childrenRemove.add(child4)
        parentFifthRepository.save(updatingParent)

        // actual

        val actual = selectList("child_seventh").size

        // assert
        assertThat(actual, not(4))
    }

    @Test
    fun `cascade가 remove이고 orphanRemoval이 true인 children을 하나 제거하지만 제거되지 않는다`() {
        // arrange
        creatParent()

        val savedParent = parentFifthRepository.findAll().last()

        createChild(savedParent.id, "child_seventh")
        createChild(savedParent.id, "child_seventh")
        createChild(savedParent.id, "child_seventh")

        val savedParent2 = parentFifthRepository.findAll().last()
        val updatingParent = savedParent2.copy()
        updatingParent.childrenRemove.removeAt(savedParent2.childrenRemove.size - 1)
        parentFifthRepository.save(updatingParent)

        // actual
        val actual = selectList("child_seventh").size

        // assert
        assertThat(actual, not(2))
    }

    @Test
    fun `cascade가 remove이고 orphanRemoval이 true인 children을 수정하지만 수정되지 않는다`() {
        // arrange
        creatParent()
        val savedParent = parentFifthRepository.findAll().last()

        createChild(savedParent.id, "child_seventh")

        val savedParent2 = parentFifthRepository.findAll().last()

        val cName = UUID.randomUUID().toString()
        val cContent = UUID.randomUUID().toString()
        val updatingChild = savedParent2.childrenRemove.last().copy(cName = cName, cContent = cContent)
        val updatingParent = savedParent2.copy(childrenRemove = mutableListOf(updatingChild))
        parentFifthRepository.saveAndFlush(updatingParent)

        // actual
        val actual = parentFifthRepository.findAll().last().childrenRemove.last()

        // assert
        assertThat(actual.cName, not(cName))
        assertThat(actual.cContent, not(cContent))
    }

    @Test
    fun `cascade가 remove이고 orphanRemoval이 true인 children을 삭제한다`() {
        // arrange
        creatParent()
        val savedParent = parentFifthRepository.findAll().last()

        createChild(savedParent.id, "child_seventh")
        createChild(savedParent.id, "child_seventh")
        createChild(savedParent.id, "child_seventh")

        parentFifthRepository.delete(savedParent)

        // actual
        val actual = selectList("child_seventh")

        // assert
        assertTrue(actual.isEmpty())
    }

    @Test
    fun `cascade가 remove이고 orphanRemoval이 false인 children을 저장하지만 저장되지 않는다`() {
        // arrange
        val child1 = ChildSeventh(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child2 = ChildSeventh(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child3 = ChildSeventh(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val children = mutableListOf(child1, child2, child3)
        val parent = ParentFifth(id = 0,
                pName = UUID.randomUUID().toString(),
                pContent = UUID.randomUUID().toString(),
                childrenRemoveRemovalFalse = children)
        parentFifthRepository.save(parent)

        // actual
        val actual = selectList("child_seventh").size

        // assert
        assertThat(actual, not(children.size))
    }

    @Test
    fun `cascade가 remove이고 orphanRemoval이 false인 children을 하나 추가하지만 추가되지 않는다`() {
        // arrange
        creatParent()

        val savedParent = parentFifthRepository.findAll().last()

        createChild(savedParent.id, "child_seventh")
        createChild(savedParent.id, "child_seventh")
        createChild(savedParent.id, "child_seventh")

        val updatingParent = savedParent.copy()
        val child4 = ChildSeventh(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        updatingParent.childrenRemoveRemovalFalse.add(child4)
        parentFifthRepository.save(updatingParent)

        // actual

        val actual = selectList("child_seventh").size

        // assert
        assertThat(actual, not(4))
    }

    @Test
    fun `cascade가 remove이고 orphanRemoval이 false인 children을 하나 제거하지만 제거되지 않는다`() {
        // arrange
        creatParent()

        val savedParent = parentFifthRepository.findAll().last()

        createChild(savedParent.id, "child_seventh")
        createChild(savedParent.id, "child_seventh")
        createChild(savedParent.id, "child_seventh")

        val savedParent2 = parentFifthRepository.findAll().last()
        val updatingParent = savedParent2.copy()
        updatingParent.childrenRemoveRemovalFalse.removeAt(savedParent2.childrenRemoveRemovalFalse.size - 1)
        parentFifthRepository.save(updatingParent)

        // actual
        val actual = selectList("child_seventh").size

        // assert
        assertThat(actual, not(2))
    }

    @Test
    fun `cascade가 remove이고 orphanRemoval이 false인 children을 수정하지만 수정되지 않는다`() {
        // arrange
        creatParent()
        val savedParent = parentFifthRepository.findAll().last()

        createChild(savedParent.id, "child_seventh")

        val savedParent2 = parentFifthRepository.findAll().last()

        val cName = UUID.randomUUID().toString()
        val cContent = UUID.randomUUID().toString()
        val updatingChild = savedParent2.childrenRemoveRemovalFalse.last().copy(cName = cName, cContent = cContent)
        val updatingParent = savedParent2.copy(childrenRemoveRemovalFalse = mutableListOf(updatingChild))
        parentFifthRepository.saveAndFlush(updatingParent)

        // actual
        val actual = parentFifthRepository.findAll().last().childrenRemoveRemovalFalse.last()

        // assert
        assertThat(actual.cName, not(cName))
        assertThat(actual.cContent, not(cContent))
    }

    @Test
    fun `cascade가 remove이고 orphanRemoval이 false인 children을 삭제한다`() {
        // arrange
        creatParent()
        val savedParent = parentFifthRepository.findAll().last()

        createChild(savedParent.id, "child_seventh")
        createChild(savedParent.id, "child_seventh")
        createChild(savedParent.id, "child_seventh")

        parentFifthRepository.delete(savedParent)

        // actual
        val actual = selectList("child_seventh")

        // assert
        assertTrue(actual.isEmpty())
    }

}