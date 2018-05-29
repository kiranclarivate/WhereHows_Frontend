variable "instance_groups" {
  default = [
    {
      name           = "MasterInstanceGroup"
      instance_role  = "MASTER"
      instance_type  = "#INSTANCE_TYPE"
      instance_count = 1
    },
    {
      name           = "CoreInstanceGroup"
      instance_role  = "CORE"
      instance_type  = "#INSTANCE_TYPE"
      instance_count = #INSTANCE_COUNT
      bid_price      = #BID_PRICE
      autoscaling_policy = <<EOF
            {
                "Constraints": {
                    "MinCapacity": 1,
                    "MaxCapacity": 6
                },
                "Rules": [
                {
                    "Name": "ScaleOutMemoryPercentage",
                    "Description": "Scale out if YARNMemoryAvailablePercentage is less than 15",
                    "Action": {
                        "SimpleScalingPolicyConfiguration": {
                            "AdjustmentType": "CHANGE_IN_CAPACITY",
                            "ScalingAdjustment": 1,
                            "CoolDown": 300
                        }
                    },
                    "Trigger": {
                        "CloudWatchAlarmDefinition": {
                            "ComparisonOperator": "LESS_THAN",
                            "EvaluationPeriods": 1,
                            "MetricName": "YARNMemoryAvailablePercentage",
                            "Namespace": "AWS/ElasticMapReduce",
                            "Period": 300,
                            "Statistic": "AVERAGE",
                            "Threshold": 15.0,
                            "Unit": "PERCENT"
                        }
                    }
                }
                ]
            }
            EOF
    },
  ]
  type = "list"
}
