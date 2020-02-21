package akka.contrib.persistence.mongodb

import akka.actor.ActorSystem
import akka.testkit._
import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.concurrent.PatienceConfiguration
import org.scalatestplus.mockito.MockitoSugar

import scala.concurrent.Await
import scala.util.Try
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

trait BaseUnitTest extends AnyFlatSpecLike with MockitoSugar with Matchers with PatienceConfiguration {

  override lazy val spanScaleFactor: Double = ConfigFactory.load().getDouble("akka.test.timefactor")

}

object ConfigLoanFixture {
  import concurrent.duration._

  def withConfig[T](config: Config, configurationRoot: String, name: String = "unit-test")(testCode: ((ActorSystem,Config)) => T):T = {
    implicit val actorSystem: ActorSystem = ActorSystem(name,config)
    val overrides = Try(config.getConfig(configurationRoot)).toOption.getOrElse(ConfigFactory.empty())
    try {
      testCode( (actorSystem, overrides) )
    } finally {
      actorSystem.terminate()
      Await.ready(actorSystem.whenTerminated, 3.seconds.dilated)
      ()
    }
  }
}
