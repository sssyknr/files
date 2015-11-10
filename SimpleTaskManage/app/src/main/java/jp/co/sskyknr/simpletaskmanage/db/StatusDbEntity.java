package jp.co.sskyknr.simpletaskmanage.db;

/**
 * 状態管理エンティティ
 */
public class StatusDbEntity {

    // ////////////////////////////////////////////////////////////////////////////////////////////
    // private フィールド
    // ////////////////////////////////////////////////////////////////////////////////////////////
    /** ID */
    private int id;
    /** 状態名 */
    private String name;
    /** シーケンスID */
    private int sequenceId;
    /** 色 */
    private String color;

    // ////////////////////////////////////////////////////////////////////////////////////////////
    // public フィールド
    // ////////////////////////////////////////////////////////////////////////////////////////////

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSequenceId(int sequenceId) {
        this.sequenceId = sequenceId;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getSequenceId() {
        return sequenceId;
    }

    public String getColor() {
        return color;
    }
}
