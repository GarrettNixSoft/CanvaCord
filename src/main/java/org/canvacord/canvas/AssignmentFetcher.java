package org.canvacord.canvas;

import edu.ksu.canvas.model.assignment.Assignment;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class AssignmentFetcher {

    public static List<Assignment> fetchAssignmentsActive(String courseID) throws IOException {

        List<Assignment> rankedAssignments = fetchAssignmentsSearch(courseID,null);

        if (rankedAssignments.size()==0){
            return rankedAssignments;
        }

        // need to return only parts of list that have due dates after the current date
        List<Assignment> ranked =  rankedAssignments.stream()
                .filter(assignment ->
                        assignment.getDueAt()!= null && assignment.getDueAt().after(Date.from(Instant.now())))
                .collect(Collectors.toList());

        return ranked;
    }

    public static List<Assignment> fetchAssignmentsSearch(String courseID, String searchTerms) throws IOException {

        CanvasApi canvasApi = CanvasApi.getInstance();

        List<Assignment> assignments = canvasApi.getAssignments(courseID);

        assignments = createRankedList(assignments, searchTerms);

        return assignments;
    }

    private static List<Assignment> createRankedList(List<Assignment> assignments, String searchTerms) {

        List<AssignmentSearchInfo> assignmentSearchInfoList = new ArrayList<>();
        // create assignmentSearchInfo objects for each assignment to allow for ranking
        // based on the number of terms that match in a search
        for (Assignment assignment : assignments) {

            AssignmentSearchInfo temp = new AssignmentSearchInfo(assignment);
            temp.setRank(searchTerms);
            assignmentSearchInfoList.add(temp);

        }
        // create a ranking based on how many of the search terms were present in the name or description

        Collections.sort(assignmentSearchInfoList);

        return assignmentSearchInfoList.stream()
                    .map(AssignmentSearchInfo::getAssignment)
                    .collect(Collectors.toList());
    }
}


class AssignmentSearchInfo implements Comparable<AssignmentSearchInfo>{
    private final String name;
    private final Assignment assignment;
    private final Date due;
    private final String description;
    private int rank;
    public AssignmentSearchInfo(Assignment assignment){
        this.assignment = assignment;
        this.name = assignment.getName();
        this.rank = 0;
        this.due = assignment.getDueAt();
        this.description = assignment.getDescription();
    }
    public int setRank(String parameters){
        if (parameters == null ) {
            return 0;
        }
        // take the string of parameters and search through the name and description of the assignment
        // the higher the rank, the more word matches are present
        for (String param:parameters.split(" ")){
            rank += (name != null && StringUtils.containsIgnoreCase(name,param))?
                    (description != null && StringUtils.containsIgnoreCase(description,param))? 2 : 1 : 0;
        }
        return this.rank;
    }
    public Assignment getAssignment(){
        return this.assignment;
    }
    @Override
    public int compareTo(AssignmentSearchInfo assignmentSearchInfo) {
        // value is < 0 if this rank is higher than the argument rank
        int rankDiff =  assignmentSearchInfo.rank - this.rank;
        int compare = 0;

        // if the ranks are the same, use the due dates as a comparison
        // we want earlier dates to be ranked higher

        // value is < 0 if this is due before the argument

        if (rankDiff!=0){
            compare = rankDiff;
        }
        else if (this.due != null && assignmentSearchInfo.due != null) {
            compare = -assignmentSearchInfo.due.compareTo(this.due);
        }
        return compare;
    }

    //TODO:
    // - create own class
    // - make it work with JSONObjects
    // - add timestamps
    // - compatability with modules
    // - ^ ranking based on date will be with updated_at
}
