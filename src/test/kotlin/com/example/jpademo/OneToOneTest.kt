package com.example.jpademo

import com.example.jpademo.model.ChildFirst
import com.example.jpademo.model.ParentFirst
import com.example.jpademo.model.ParentFirstRepository
import org.hamcrest.Matchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.junit4.SpringRunner
import java.util.*


@Suppress("NonAsciiCharacters")
@RunWith(SpringRunner::class)
@DataJpaTest
class OneToOneTest {

    @Autowired
    private lateinit var parentFirstRepository: ParentFirstRepository

    @Test
    fun `parent를 저장하면 child도 저장된다`() {
        // arrange
        val cName = UUID.randomUUID().toString()
        val cContent = UUID.randomUUID().toString()
        val child = ChildFirst(0, cName, cContent)
        val parent = ParentFirst(0, UUID.randomUUID().toString(), UUID.randomUUID().toString(), child)
        parentFirstRepository.save(parent)

        // actual
        val actual = parentFirstRepository.findAll().last().child

        // assert
        assertThat(actual.cName, `is`(cName))
        assertThat(actual.cContent, `is`(cContent))
    }

    @Test
    fun `parent를 수정하면 child도 수정된다`() {
        // arrange
        val child = ChildFirst(0, "", "")
        val parent = ParentFirst(0, UUID.randomUUID().toString(), UUID.randomUUID().toString(), child)
        parentFirstRepository.save(parent)

        val savedParent = parentFirstRepository.findAll().last()
        val cName = UUID.randomUUID().toString()
        val cContent = UUID.randomUUID().toString()
        val updatingParent = savedParent.copy(
                child = savedParent.child.copy(cName = cName, cContent = cContent))
        parentFirstRepository.saveAndFlush(updatingParent)

        // actual
        val actual = parentFirstRepository.findAll().last().child

        // assert
        assertThat(actual.cName, `is`(cName))
        assertThat(actual.cContent, `is`(cContent))
    }

    @Test
    fun `parent를 삭제하면 child가 삭제된다`() {
        // arrange
        val child = ChildFirst(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val parent = ParentFirst(0, UUID.randomUUID().toString(), UUID.randomUUID().toString(), child)
        parentFirstRepository.save(parent)

        parentFirstRepository.delete(parent)

        // actual
        val actual = parentFirstRepository.findAll()

        // assert
        Assert.assertTrue(actual.isEmpty())
    }


}