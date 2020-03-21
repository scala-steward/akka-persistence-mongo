package akka.contrib.persistence.mongodb

import akka.actor.ActorSystem
import com.typesafe.config.{Config, ConfigFactory}

class MongoExtensionSpec extends BaseUnitTest {

  val driver = ConfigFactory.parseString(
    s"""
      |akka.contrib.persistence.mongodb.mongo.driver="${classOf[StubbyMongoPersistenceExtension].getName}"
    """.stripMargin)

  "A mongo extension" should "load and verify the delivered configuration" in {
    val system = ActorSystem("unit-test",driver)

    try {
      MongoPersistenceExtension.get(system) // Should not throw an exception
      ()
    } finally {
      system.terminate()
      ()
    }
  }

}

class StubbyMongoPersistenceExtension(actorSystem: ActorSystem) extends MongoPersistenceExtension(actorSystem) {

  case class StubbyConfiguredExtension(config: Config) extends ConfiguredExtension {
    override def journaler: MongoPersistenceJournallingApi = null

    override def snapshotter: MongoPersistenceSnapshottingApi = null

    override def readJournal: MongoPersistenceReadJournallingApi = null
  }

  override def configured(config: Config): ConfiguredExtension = StubbyConfiguredExtension(config)
}

