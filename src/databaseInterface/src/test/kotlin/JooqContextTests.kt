import org.junit.Test
import org.mrc.ide.auth.db.JooqContext

class JooqContextTests {

    @Test
    fun canConnect() {
        JooqContext().use {

        }
    }
}