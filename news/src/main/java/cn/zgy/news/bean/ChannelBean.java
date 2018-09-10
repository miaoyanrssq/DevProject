package cn.zgy.news.bean;


/**
 * 频道 - JavaBean
 *
 * @author a_liYa
 * @date 2017/7/23 18:45.
 */
public class ChannelBean {

    private Long id;
    private String name;
    private boolean collapsed; // true：固定；false：可排序
    private boolean selected;
    private String nav_type = "";
    private String nav_parameter;
    private int sortKey;

    public boolean tempUnconcern; // true，临时取消


    public ChannelBean(Long id, String name, boolean collapsed, boolean selected,
                       String nav_type, String nav_parameter, int sortKey) {
        this.id = id;
        this.name = name;
        this.collapsed = collapsed;
        this.selected = selected;
        this.nav_type = nav_type;
        this.nav_parameter = nav_parameter;
        this.sortKey = sortKey;
    }

    public ChannelBean() {
    }


    /**
     * 是否可拖拽
     *
     * @return true:可拖拽；false:不可
     */
    public boolean canDrag() {
        return !isCollapsed() && isSelected();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCollapsed() {
        return collapsed;
    }

    public void setCollapsed(boolean collapsed) {
        this.collapsed = collapsed;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean getCollapsed() {
        return this.collapsed;
    }

    public boolean getSelected() {
        return this.selected;
    }

    public String getNav_type() {
        return nav_type;
    }

    public void setNav_type(String nav_type) {
        this.nav_type = nav_type;
    }

    public String getNav_parameter() {
        return nav_parameter;
    }

    public void setNav_parameter(String nav_parameter) {
        this.nav_parameter = nav_parameter;
    }

    public int getSortKey() {
        return sortKey;
    }

    public void setSortKey(int sortKey) {
        this.sortKey = sortKey;
    }

    public static final class Builder {
        private Long id;
        private String name;
        private boolean collapsed;
        private boolean selected;
        private int sort_number;

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder collapsed(boolean val) {
            collapsed = val;
            return this;
        }

        public Builder selected(boolean val) {
            selected = val;
            return this;
        }

        public Builder sort_number(int val) {
            sort_number = val;
            return this;
        }

        public Builder id(Long val) {
            id = val;
            return this;
        }

        public ChannelBean build() {
            ChannelBean channelBean = new ChannelBean();
            channelBean.setId(id);
            channelBean.setName(name);
            channelBean.setCollapsed(collapsed);
            channelBean.setSelected(selected);
            return channelBean;
        }
    }

}
