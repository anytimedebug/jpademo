package com.example.jpademo

import com.example.jpademo.model.ChildSecond
import com.example.jpademo.model.ParentSecond
import com.example.jpademo.model.ParentSecondRepository
import org.hamcrest.Matchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner
import java.util.*


@Suppress("NonAsciiCharacters")
@RunWith(SpringRunner::class)
@DataJpaTest
class OneToOnePrimaryKeyJoinTest {

    @Autowired
    private lateinit var parentSecondRepository: ParentSecondRepository

    @Test
    fun `parent를 저장하면 child도 저장된다`() {
        // arrange
        val cName = UUID.randomUUID().toString()
        val cContent = UUID.randomUUID().toString()
        val child = ChildSecond(0, cName, cContent)
        val parent = ParentSecond(0, UUID.randomUUID().toString(), UUID.randomUUID().toString(), child)
        parentSecondRepository.save(parent)

        // actual
        val actual = parentSecondRepository.findAll().last().child

        // assertR
        assertThat(actual.cName, `is`(cName))
        assertThat(actual.cContent, `is`(cContent))
    }

    @Test
    fun `parent를 수정하면 child도 수정된다`() {
        // arrange
        val child = ChildSecond(0, "", "")
        val parent = ParentSecond(0, UUID.randomUUID().toString(), UUID.randomUUID().toString(), child)
        parentSecondRepository.save(parent)

        val savedParent = parentSecondRepository.findAll().last()
        val cName = UUID.randomUUID().toString()
        val cContent = UUID.randomUUID().toString()
        val updatingParent = savedParent.copy(
                child = savedParent.child.copy(cName = cName, cContent = cContent))
        parentSecondRepository.saveAndFlush(updatingParent)

        // actual
        val actual = parentSecondRepository.findAll().last().child

        // assert
        assertThat(actual.cName, `is`(cName))
        assertThat(actual.cContent, `is`(cContent))
    }

    @Test
    fun `parent를 삭제하면 child가 삭제된다`() {
        // arrange
        val child = ChildSecond(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val parent = ParentSecond(0, UUID.randomUUID().toString(), UUID.randomUUID().toString(), child)
        parentSecondRepository.save(parent)

        parentSecondRepository.delete(parent)

        // actual
        val actual = parentSecondRepository.findAll()

        // assert
        Assert.assertTrue(actual.isEmpty())
    }


}