package hk.hku.examoverflowhku.Model;

import net.sourceforge.jtds.jdbc.DateTime;

public class Solution {
    private String questionId;
    private String solutionTitle;
    private String solutionContent;

    public DateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(DateTime timestamp) {
        this.timestamp = timestamp;
    }

    private DateTime timestamp;

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getSolutionTitle() {
        return solutionTitle;
    }

    public void setSolutionTitle(String solutionTitle) {
        this.solutionTitle = solutionTitle;
    }

    public String getSolutionContent() {
        return solutionContent;
    }

    public void setSolutionContent(String solutionContent) {
        this.solutionContent = solutionContent;
    }
}
