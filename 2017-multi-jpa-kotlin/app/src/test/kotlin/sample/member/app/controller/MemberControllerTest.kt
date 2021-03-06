package sample.member.app.controller

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.servlet.ViewResolver
import org.springframework.web.servlet.view.json.MappingJackson2JsonView
import sample.member.app.MainApplication
import sample.member.app.web.member.MemberRestController
import sample.member.app.web.member.MemberService
import sample.member.domain.model.UserEntity


@RunWith(SpringRunner::class)
@SpringBootTest(classes = [MainApplication::class])
class MemberControllerTest {

	//	@Autowired
//	protected WebApplicationContext wac
	protected lateinit var mockMvc: MockMvc

	@InjectMocks
	private lateinit var controller: MemberRestController
	@Mock
	private lateinit var service: MemberService

	//given
	@Before
	fun setupMock() {
		MockitoAnnotations.initMocks(this)
		mockMvc = MockMvcBuilders.standaloneSetup(controller)
				.setCustomArgumentResolvers(PageableHandlerMethodArgumentResolver())
				.setViewResolvers(ViewResolver { viewName, locale -> MappingJackson2JsonView() })
				.build()
	}

	@Test
	fun `test friend list rest controller`() {
		val pageable = PageRequest.of(0, 10)
		Mockito.`when`(service.list(pageable)).thenReturn(PageImpl<UserEntity>(
				(1..10).map { idx ->
					UserEntity(
							"username$idx",
							"realname$idx",
							"email$idx@email.com",
							"passw0rd$idx"
					)
				},
				pageable,
				10
		))
		mockMvc.perform(MockMvcRequestBuilders.get("/member/rest/list")
				.param("page", "0").param("size", "10"))
				.andExpect(MockMvcResultMatchers.status().isOk())

		Mockito.verify(service).list(pageable)
		Mockito.verifyNoMoreInteractions(service)
	}

	@Test
	fun `test friend detail rest controller`() {
		val idx = 1
		val userEntity = UserEntity(
				"username$idx",
				"realname$idx",
				"email$idx@email.com",
				"passw0rd$idx"
		)
		Mockito.`when`(service.detail("username1")).thenReturn(userEntity)
//		Mockito.doReturn(userEntity).`when`(service).detail("username1")
		mockMvc.perform(MockMvcRequestBuilders.get("/member/rest/detail")
				.param("username", "username1"))
				.andExpect(MockMvcResultMatchers.status().isOk)

		Mockito.verify(service).detail("username1")
		Mockito.verifyNoMoreInteractions(service)
	}

}
