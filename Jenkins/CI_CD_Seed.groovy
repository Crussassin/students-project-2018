pelineJob("CI_Job") {
	triggers {
		scm('H/5 * * * *')
	}
	definition {
		cpsScm {
			scm {
				git {
					remote {
						url("https://github.com/Crussassin/students-project-2018")
						credentials("jenkins-github")
					}
					branch("refs/tags/*")
				}
			}
			scriptPath("Jenkins/CI_job.groovy")
		}
	}
}

pipelineJob("CD_job") {
    triggers {
	upstream('CI_job')
    }
    parameters {
	gitParam('CONTAINER_TAG') {
	    description('Select tag of image')
	    type('TAG')
	    sortMode('DESCENDING_SMART')
	    defaultValue('')
        }
    }
    definition {
        cpsScm {
            scm {
                git {
                    remote {
                        url("https://github.com/Crussassin/students-project-2018")
                        credentials("jenkins-github")
                    }
                    branch("refs/tags/*")
                }
            }
            scriptPath("Jenkins/CD_job.groovy")
        }
    }
}
