@Library('codeflow') _
node("mesos") {
    stage("init") {
        cloudBuildConfig {
            group_id = "g8b372f2189f44b018a12bc8fa9bea5cc"
            service_id = "a94dff66b5e8409ca358fac1cf4aafda"
            project_id = "532d1f0c8d564daf9655b2a5f284259b"
            project_name = "Location_DeveloperSample"
            gate_name = "LocationDemo_CodeGate"
        }
    }

    cloudBuild {
        jobs = [
            "cmetrics": {
                cloudDragonGate()
            },
            "codingstylecheck": {
                cloudDragonGate()
            },
            "findbugs": {
                cloudDragonGate()
            },
        ]
    }
}

