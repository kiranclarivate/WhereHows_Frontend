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

  step {
    name="my-spark-program"
    action_on_failure = "TERMINATE_CLUSTER"
    hadoop_jar_step {
    jar="command-runner.jar"
    args = ["spark-submit", "--deploy-mode","cluster","--class", "org.apache.spark.examples.JavaWordCount","#JAR_PATH", "s3://richardx-terraform-test/test1/readme.txt"]
    }
    # keep_job_flow_alive_when_no_steps = "off"
  }
  scale_down_behavior = "TERMINATE_AT_TASK_COMPLETION"
  log_uri = "s3://aws-logs-509786517216-us-west-2/elasticmapreduce/"

  visible_to_all_users = true
  termination_protection = false
}
