package app_start

import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mrc.ide.auth.api.ActionContext
import org.mrc.ide.auth.api.controllers.Controller
import org.mrc.ide.auth.api.routing.Endpoint
import org.mrc.ide.auth.api.routing.Router

class RouterTests
{
    @Test
    fun `router can invoke action`()
    {
        TestController.invoked = false
        val router = Router(mock(), mock())
        router.invokeControllerAction(Endpoint("/", TestController::class, "test"), mock())
        assertThat(TestController.invoked).isTrue()
    }

    class TestController(
            context: ActionContext
    ) : Controller(context)
    {
        fun test()
        {
            invoked = true
        }

        companion object
        {
            var invoked = false
        }
    }
}