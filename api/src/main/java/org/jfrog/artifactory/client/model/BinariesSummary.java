package org.jfrog.artifactory.client.model;

/**
 * @author Aviad Shikloshi
 */
public interface BinariesSummary {

    String getBinariesCount();

    String getBinariesSize();

    String getArtifactsSize();

    String getArtifactsCount();

    String getOptimization();

    String getItemsCount();

}
