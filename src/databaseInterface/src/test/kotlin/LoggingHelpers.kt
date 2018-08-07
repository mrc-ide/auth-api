import java.util.logging.Level
import java.util.logging.Logger

// Note that this function disables logging for java.util.logging loggers, i.e.
// as used by the postgres library. Most modern Java projects (including our one)
// use SL4J instead, and so you could interact with logging another way
fun <T> disableLoggingFrom(loggerName: String, action: () -> T): T
{
    val logger = Logger.getLogger(loggerName)
    val oldLevel = logger.level
    logger.level = Level.OFF
    return try
    {
        action()
    }
    finally
    {
        logger.level = oldLevel
    }
}