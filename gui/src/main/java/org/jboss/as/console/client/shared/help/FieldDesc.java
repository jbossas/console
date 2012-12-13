package org.jboss.as.console.client.shared.help;

/**
 * @author Heiko Braun
 * @date 12/4/12
 */
public class FieldDesc {
    String ref;
    String desc;
    private boolean expressionSupport;

    public FieldDesc(String ref, String desc) {
        this.ref = ref;
        this.desc = desc;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String title) {
        this.desc = title;
    }

    public boolean doesSupportExpressions(){
        return this.expressionSupport;
    }

    public void setSupportExpressions(boolean b) {
        this.expressionSupport = b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FieldDesc)) return false;

        FieldDesc fieldDesc = (FieldDesc) o;

        if (!ref.equals(fieldDesc.ref)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return ref.hashCode();
    }
}
