def gitUrl = 'https://github.com/crussassin/students-project-2018.git'

pipelineJob("CI_Job") {
	triggers {
		scm('H/5 * * * *')
	}
	definition {
		cpsScm {
			scm {
				git {
					remote {
						url(gitUrl)
						credentials("Crussassin-github")
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
	    description('')
		branch('refs/tags/*')
	    type('TAG')
	    sortMode('DESCENDING_SMART')
	    defaultValue('latest')
        }
    }
    definition {
        cpsScm {
            scm {
                git {
                    remote {
                        url(gitUrl)
                        credentials("Crussassin-github")
                    }
                    branch("refs/tags/*")
                }
            }
            scriptPath("Jenkins/CD_job.groovy")
        }
    }
}
