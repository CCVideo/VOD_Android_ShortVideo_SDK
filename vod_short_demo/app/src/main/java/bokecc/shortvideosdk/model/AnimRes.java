package bokecc.shortvideosdk.model;

public class AnimRes {
    private int normalImgRes;
    private int selectdImgRes;
    private boolean isSelected = false;
    private String animName;
    private int animType;

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

    public String getAnimName() {
        return animName;
    }

    public void setAnimName(String animName) {
        this.animName = animName;
    }

    public int getAnimType() {
        return animType;
    }

    public void setAnimType(int animType) {
        this.animType = animType;
    }
}
