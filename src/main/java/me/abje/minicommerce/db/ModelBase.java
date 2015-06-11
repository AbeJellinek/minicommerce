package me.abje.minicommerce.db;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@MappedSuperclass
public class ModelBase {
    /**
     * For MySQL: The maximum length of a varchar, in characters, if it contains UTF-8 data and it's marked as UNIQUE.
     * This is a limitation of MySQL's support for true UTF-8, which it calls "utf8mb4."
     */
    protected static final int MYSQL_MAX_UNIQUE_UTF8MB4_VARCHAR_LENGTH = 190;

    @Id
    @GeneratedValue
    private Integer id;

    @Version
    private int version;

    /**
     * The date/time that this object was created.
     */
    @NotNull
    private final long created;

    public ModelBase() {
        created = System.currentTimeMillis();
    }

    public Integer getId() {
        return id;
    }

    public int getVersion() {
        return version;
    }

    public long getCreated() {
        return created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ModelBase)) {
            return false;
        }

        ModelBase modelBase = (ModelBase) o;

        return created == modelBase.created && version == modelBase.version &&
                !(id != null ? !id.equals(modelBase.id) : modelBase.id != null);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + version;
        result = 31 * result + (int) (created ^ (created >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "ModelBase" + "{created=" + created + ", id=" + id + ", version=" + version + '}';
    }
}
