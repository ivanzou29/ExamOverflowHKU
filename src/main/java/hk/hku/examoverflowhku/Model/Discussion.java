package hk.hku.examoverflowhku.Model;

import net.sourceforge.jtds.jdbc.DateTime;

public class Discussion {
    private String issueTitle;
    private String discussionId;
    private String discussionContent;

    public DateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(DateTime timestamp) {
        this.timestamp = timestamp;
    }

    private DateTime timestamp;

    public String getIssueTitle() {
        return issueTitle;
    }

    public void setIssueTitle(String issueTitle) {
        this.issueTitle = issueTitle;
    }

    public String getDiscussionId() {
        return discussionId;
    }

    public void setDiscussionId(String discussionId) {
        this.discussionId = discussionId;
    }

    public String getDiscussionContent() {
        return discussionContent;
    }

    public void setDiscussionContent(String discussionContent) {
        this.discussionContent = discussionContent;
    }
}

