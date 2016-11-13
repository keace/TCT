package ua.kyslytsia.tct.mocks;

public class StageOnAttempt {
    private long id;
    private long attempt_id;
    private long stage_on_competition_id;
    private long penalty;

    public StageOnAttempt(long stage_on_competition_id, long penalty) {
        this.stage_on_competition_id = stage_on_competition_id;
        this.penalty = penalty;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAttempt_id() {
        return attempt_id;
    }

    public void setAttempt_id(long attempt_id) {
        this.attempt_id = attempt_id;
    }

    public long getStage_on_competition_id() {
        return stage_on_competition_id;
    }

    public void setStage_on_competition_id(long stage_on_competition_id) {
        this.stage_on_competition_id = stage_on_competition_id;
    }

    public long getPenalty() {
        return penalty;
    }

    public void setPenalty(long penalty) {
        this.penalty = penalty;
    }

    @Override
    public String toString() {
        return "StageOnAttempt{" +
                "id=" + id +
                ", attempt_id=" + attempt_id +
                ", stage_on_competition_id=" + stage_on_competition_id +
                ", penalty=" + penalty +
                '}';
    }
}
