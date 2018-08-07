import org.mrc.ide.auth.db.JooqContext
import org.mrc.ide.auth.db.Tables
import org.mrc.ide.auth.db.Tables.APP_USER
import org.mrc.ide.auth.models.permissions.ReifiedRole
import org.mrc.ide.auth.security.SodiumPasswordEncoder

fun hashedPassword(plainPassword: String) = SodiumPasswordEncoder().encode(plainPassword)

fun JooqContext.addUserForTesting(
        username: String,
        name: String = "Test User",
        email: String = "$username@example.com",
        password: String = "password"
)
{
    this.dsl.newRecord(APP_USER).apply {
        this.username = username
        this.name = name
        this.email = email
        this.passwordHash = hashedPassword(password)
    }.store()

    this.dsl.newRecord(Tables.USER_GROUP).apply {
        this.name = username
        id = username
    }.insert()

    this.dsl.newRecord(Tables.USER_GROUP_MEMBERSHIP).apply {
        this.username = username
        userGroup = username
    }.insert()
}

fun JooqContext.addUserWithRoles(username: String, vararg roles: ReifiedRole)
{
    this.addUserForTesting(username)
    for (role in roles)
    {
        this.ensureUserHasRole(username, role)
    }
}