package bokecc.shortvideosdk.model;

public class TransitionRes {
    private int normalImgRes;
    private int selectdImgRes;
    private boolean isSelected = false;
    private String transitionName;
    private int transitionType;

    public int getNormalImgRes() {
        return normalImgRes;
    }

    public void setNormalImgRes(int normalImgRes) {
        this.normalImgRes = normalImgRes;
    }

    public int getSelectdImgRes() {
        return selectdImgRes;
    }

    public void setSelectdImgRes(int selectdImgRes) {
        this.selectdImgRes = selectdImgRes;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getTransitionName() {
        return transitionName;
    }

    public void setTransitionName(String transitionName) {
        this.transitionName = transitionName;
    }

    public int getTransitionType() {
        return transitionType;
    }

    public void setTransitionType(int transitionType) {
        this.transitionType = transitionType;
    }
}
