package bitbucketpullrequestbuilder.bitbucketpullrequestbuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import hudson.EnvVars;
import hudson.FilePath;
import jenkins.model.Jenkins;

import java.io.File;
import java.util.Objects;
import java.util.logging.Logger;

class BuildResultLoader {
    private static final Logger logger = Logger.getLogger(BitbucketBuildListener.class.getName());
    private BuildResult buildResult;
    private static final String resultFileName = "ci_bitbucket_comment.json";

    BuildResultLoader(EnvVars env) {
        File file;
        if (env == null) {
            return;
        }

        buildResult = new BuildResult();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String path = env.get("WORKSPACE") + File.separator + resultFileName;
            String nodeName = env.get("NODE_NAME");

            if (nodeName == null || nodeName.equals("master")) {
                logger.fine("BitbucketBuildListener checking result file on default node");
                file = new File(path);
                buildResult = objectMapper.readValue(file, BuildResult.class);
            } else {
                logger.fine("BitbucketBuildListener checking result file on slave node: " + nodeName);
                FilePath fp = new FilePath(
                        Objects.requireNonNull(
                                Objects.requireNonNull(Jenkins.getInstance()).getNode(env.get("NODE_NAME"))
                        ).getChannel(),
                        path
                );

                logger.fine("BitbucketBuildListener BuildResult loading from string");

                String fileContent = fp.readToString();

                logger.fine("BitbucketBuildListener BuildResult content string length: " + fileContent.length());

                buildResult = objectMapper.readValue(fileContent, BuildResult.class);
            }

            // check build number
            Integer buildNumber = Integer.valueOf(env.get("BUILD_NUMBER"));

            logger.fine(
                    "BitbucketBuildListener BUILD_NUMBER: " + buildNumber + "; in result: " + buildResult.build_number
            );

            if (!buildNumber.equals(buildResult.build_number)) {
                logger.fine("BitbucketBuildListener wrong build triggers buildResult reset");
            }
        } catch (Exception e) {
            logger.fine(e.getMessage());
            logger.fine("BitbucketBuildListener BuildResult was ignored.");
        }
    }

    public BuildResult getBuildResult() {
        return buildResult;
    }
}
