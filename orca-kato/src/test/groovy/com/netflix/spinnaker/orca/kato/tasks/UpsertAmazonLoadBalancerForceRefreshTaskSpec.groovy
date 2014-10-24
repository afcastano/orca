package com.netflix.spinnaker.orca.kato.tasks

import com.netflix.spinnaker.orca.oort.OortService
import com.netflix.spinnaker.orca.pipeline.PipelineStage
import spock.lang.Specification
import spock.lang.Subject

/**
 * Created by aglover on 9/29/14.
 */
class UpsertAmazonLoadBalancerForceRefreshTaskSpec extends Specification {
  @Subject task = new UpsertAmazonLoadBalancerForceRefreshTask()
  def stage = new PipelineStage("whatever")

  def config = [
    "account.name"  : "fzlem",
    region          : ["us-west-1"],
    credentials     : "fzlem"
  ]

  def setup() {
    stage.updateContext(config)
  }

  void "should force cache refresh server groups via oort when clusterName provided"() {
    setup:
    def name = "flapjack"
    stage.updateContext(clusterName: name)
    task.oort = Mock(OortService)

    when:
    task.execute(stage)

    then:
    1 * task.oort.forceCacheUpdate(UpsertAmazonLoadBalancerForceRefreshTask.REFRESH_TYPE, _) >> { String type, Map<String, ? extends Object> body ->
      assert body.loadBalancerName == "$name-frontend"
      assert body.account == config."account.name"
      assert body.region == "us-west-1"
    }
  }

  void "should force cache refresh server groups via oort when name provided"() {
    setup:
    def name = "flapjack-frontend"
    stage.updateContext(name: name)
    task.oort = Mock(OortService)

    when:
    task.execute(stage)

    then:
    1 * task.oort.forceCacheUpdate(UpsertAmazonLoadBalancerForceRefreshTask.REFRESH_TYPE, _) >> { String type, Map<String, ? extends Object> body ->
      assert body.loadBalancerName == name
      assert body.account == config."account.name"
      assert body.region == "us-west-1"
    }
  }
}
