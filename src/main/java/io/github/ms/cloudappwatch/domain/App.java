package io.github.ms.cloudappwatch.domain;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

import java.io.Serializable;

import io.github.ms.cloudappwatch.domain.enumeration.AppStatus;

/**
 * A App.
 */
@Entity
@Table(name = "app")
public class App implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "command_line")
    private String commandLine;

    @Column(name = "service_flag")
    private Boolean serviceFlag;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AppStatus status;

    @ManyToOne
    @JsonIgnoreProperties("apps")
    private Server server;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCommandLine() {
        return commandLine;
    }

    public App commandLine(String commandLine) {
        this.commandLine = commandLine;
        return this;
    }

    public void setCommandLine(String commandLine) {
        this.commandLine = commandLine;
    }

    public Boolean isServiceFlag() {
        return serviceFlag;
    }

    public App serviceFlag(Boolean serviceFlag) {
        this.serviceFlag = serviceFlag;
        return this;
    }

    public void setServiceFlag(Boolean serviceFlag) {
        this.serviceFlag = serviceFlag;
    }

    public AppStatus getStatus() {
        return status;
    }

    public App status(AppStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(AppStatus status) {
        this.status = status;
    }

    public Server getServer() {
        return server;
    }

    public App server(Server server) {
        this.server = server;
        return this;
    }

    public void setServer(Server server) {
        this.server = server;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof App)) {
            return false;
        }
        return id != null && id.equals(((App) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "App{" +
            "id=" + getId() +
            ", commandLine='" + getCommandLine() + "'" +
            ", serviceFlag='" + isServiceFlag() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
