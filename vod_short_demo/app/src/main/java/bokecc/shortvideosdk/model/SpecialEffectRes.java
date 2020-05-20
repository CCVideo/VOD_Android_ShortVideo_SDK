package bokecc.shortvideosdk.model;

public class SpecialEffectRes {
    private int normalImgRes;
    private int selectdImgRes;
    private boolean isSelected = false;
    private String spEffectName;

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

    public String getSpEffectName() {
        return spEffectName;
    }

    public void setSpEffectName(String spEffectName) {
        this.spEffectName = spEffectName;
    }
}
