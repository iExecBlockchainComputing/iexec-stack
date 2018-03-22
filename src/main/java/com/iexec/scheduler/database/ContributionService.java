package com.iexec.scheduler.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ContributionService {

    private static final Logger log = LoggerFactory.getLogger(ContributionService.class);

    private Map<String, List<String>> calledWorkerMap;
    private Map<String, List<Contribution>> contributionMap;

    public ContributionService() {
        this.calledWorkerMap = new HashMap<>();
        this.contributionMap = new HashMap<>();
    }

    public void setCalledWorker(String workOrderId, List<String> workers) {
        if (getCalledWorkers(workOrderId) == null && getContributions(workOrderId) == null) {
            calledWorkerMap.put(workOrderId, workers);
            contributionMap.put(workOrderId, new ArrayList<>());
        }
    }

    public boolean addContribution(Contribution contribution) {
        String workOrderId = contribution.getWorkOrderId();
        String worker = contribution.getWorker();
        return hasWorkerBeenCalled(workOrderId, worker)
                && !hasWorkerAlreadyContributed(workOrderId, worker)
                && getContributions(workOrderId).add(contribution);
    }

    public boolean hasAllWorkerContributed(String workOrderId) {
        return getContributions(workOrderId).size() == getCalledWorkers(workOrderId).size();
    }

    private boolean hasWorkerBeenCalled(String workOrderId, String worker) {
        return getCalledWorkers(workOrderId).contains(worker);
    }

    private boolean hasWorkerAlreadyContributed(String workOrderId, String worker) {
        for (Contribution contribution : getContributions(workOrderId)) {
            if (contribution.getWorker().equals(worker)) {
                return true;
            }
        }
        return false;
    }

    private List<Contribution> getContributions(String workOrderId) {
        return contributionMap.get(workOrderId);
    }

    private List<String> getCalledWorkers(String workOrderId) {
        return calledWorkerMap.get(workOrderId);
    }

}
