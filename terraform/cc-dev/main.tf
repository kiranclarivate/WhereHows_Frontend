data "template_file" "emr_configurations" {
  template = "${file("configurations.json")}"
}

resource "aws_emr_cluster" "emr-test-cluster" {
  name          = "#CLUSTER_NAME"
  release_label = "emr-5.12.0"
  applications  = #APPLICATIONS

  termination_protection = false
  keep_job_flow_alive_when_no_steps = true

  ec2_attributes {
    subnet_id                         = "subnet-a93c6ede"
    emr_managed_master_security_group = "sg-13eca968"
    emr_managed_slave_security_group  = "sg-cce9acb7"
    additional_master_security_groups = "sg-f6166289"
    instance_profile                  = "svc-aws-emr-ec2-default"
  }
  
  instance_group = "${var.instance_groups}"

  ebs_root_volume_size     = 100

  tags {
    application = "my-test-app"
    created_by = "Richard Xin"
  }
  
  service_role = "arn:aws:iam::509786517216:role/cl/svc/aws/svc-aws-emr-default"
  autoscaling_role = "arn:aws:iam::509786517216:role/cl/svc/aws/svc-aws-emr-autoscaling"

  step {
    name="my-spark-program"
    action_on_failure = "TERMINATE_CLUSTER"
    hadoop_jar_step {
    jar="command-runner.jar"
    args = [#SPARK_ARGS,"--class","#CLASS_NAME","#JAR_PATH",#PROGRAM_ARGS]
    }
  }
  scale_down_behavior = "TERMINATE_AT_TASK_COMPLETION"
  log_uri = "s3://aws-logs-509786517216-us-west-2/elasticmapreduce/"
  keep_job_flow_alive_when_no_steps = false
  visible_to_all_users = true

}
