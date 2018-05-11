resource "aws_emr_cluster" "emr-test-cluster" {
  name          = "richardx-test-terraform"
  release_label = "emr-5.12.0"
  applications  = ["Hadoop", "Hive", "Spark", "Zeppelin", "Livy"]

  termination_protection = false
  keep_job_flow_alive_when_no_steps = true

  ec2_attributes {
    subnet_id                         = "subnet-28028a5f"
    emr_managed_master_security_group = "sg-aec499d5"
    emr_managed_slave_security_group  = "sg-72c59809"
    instance_profile                  = "svc-aws-emr-ec2-default"
  }
  
  instance_group = "${var.instance_groups}"

  ebs_root_volume_size     = 100

  tags {
    role     = "created_by"
    env      = "Richard Xin"
  }

  configurations = "configurations.json"

  service_role = "arn:aws:iam::369874303498:role/cl/svc/aws/svc-aws-emr-default"
}
