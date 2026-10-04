public class Section {

    private String sectionId;
    private String stadiumId;
    private String sectionName;

    public Section(String sectionId, String stadiumId, String sectionName) {
        this.sectionId = sectionId;
        this.stadiumId = stadiumId;
        this.sectionName = sectionName;
    }

    public String getSectionId() {
        return sectionId;
    }

    public String getStadiumId() {
        return stadiumId;
    }

    public String getSectionName() {
        return sectionName;
    }

    @Override
    public String toString() {
        return "Section{"
                + "sectionId='" + sectionId + '\''
                + ", stadiumId='" + stadiumId + '\''
                + ", sectionName='" + sectionName + '\''
                + '}';
    }
}