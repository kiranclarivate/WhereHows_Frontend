data "template_file" "emr_configurations" {
  template = "${file("configurations.json")}"
}

resource "aws_emr_cluster" "cluster" {
  name          = "#CLUSTER_NAME"
  release_label = "emr-5.12.0"
  applications  = #APPLICATIONS

  termination_protection = false
  keep_job_flow_alive_when_no_steps = false

  ec2_attributes = "${var.#AWS_ACCOUNT_ec2_attributes}"
  
  instance_group = "${var.instance_groups}"

  ebs_root_volume_size     = 100

  tags {
    application = "my-test-app"
    created_by = "#CREATED_BY"
    cost_center = "12345"
    user_env = "#AWS_ACCOUNT"
  }
  
  service_role = "arn:aws:iam::509786517216:role/cl/svc/aws/svc-aws-emr-default"
  autoscaling_role = "arn:aws:iam::509786517216:role/cl/svc/aws/svc-aws-emr-autoscaling"

  step {
    name="#STEP_NAME"
    action_on_failure = "TERMINATE_CLUSTER"
    hadoop_jar_step {
    jar="command-runner.jar"
    args = [#SPARK_ARGS,"--class","#CLASS_NAME","#JAR_PATH",#PROGRAM_ARGS]
    }
  }
  scale_down_behavior = "TERMINATE_AT_TASK_COMPLETION"
  log_uri = "s3://aws-logs-509786517216-us-west-2/elasticmapreduce/"

  visible_to_all_users = true

}
