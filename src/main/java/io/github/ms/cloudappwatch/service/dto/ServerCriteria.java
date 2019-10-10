package io.github.ms.cloudappwatch.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import io.github.ms.cloudappwatch.domain.enumeration.ServiceStatus;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.ZonedDateTimeFilter;

/**
 * Criteria class for the {@link io.github.ms.cloudappwatch.domain.Server} entity. This class is used
 * in {@link io.github.ms.cloudappwatch.web.rest.ServerResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /servers?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ServerCriteria implements Serializable, Criteria {
    /**
     * Class for filtering ServiceStatus
     */
    public static class ServiceStatusFilter extends Filter<ServiceStatus> {

        public ServiceStatusFilter() {
        }

        public ServiceStatusFilter(ServiceStatusFilter filter) {
            super(filter);
        }

        @Override
        public ServiceStatusFilter copy() {
            return new ServiceStatusFilter(this);
        }

    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter hostName;

    private ServiceStatusFilter status;

    private ZonedDateTimeFilter lastCheck;

    private LongFilter adminId;

    public ServerCriteria(){
    }

    public ServerCriteria(ServerCriteria other){
        this.id = other.id == null ? null : other.id.copy();
        this.hostName = other.hostName == null ? null : other.hostName.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.lastCheck = other.lastCheck == null ? null : other.lastCheck.copy();
        this.adminId = other.adminId == null ? null : other.adminId.copy();
    }

    @Override
    public ServerCriteria copy() {
        return new ServerCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getHostName() {
        return hostName;
    }

    public void setHostName(StringFilter hostName) {
        this.hostName = hostName;
    }

    public ServiceStatusFilter getStatus() {
        return status;
    }

    public void setStatus(ServiceStatusFilter status) {
        this.status = status;
    }

    public ZonedDateTimeFilter getLastCheck() {
        return lastCheck;
    }

    public void setLastCheck(ZonedDateTimeFilter lastCheck) {
        this.lastCheck = lastCheck;
    }

    public LongFilter getAdminId() {
        return adminId;
    }

    public void setAdminId(LongFilter adminId) {
        this.adminId = adminId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ServerCriteria that = (ServerCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(hostName, that.hostName) &&
            Objects.equals(status, that.status) &&
            Objects.equals(lastCheck, that.lastCheck) &&
            Objects.equals(adminId, that.adminId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        hostName,
        status,
        lastCheck,
        adminId
        );
    }

    @Override
    public String toString() {
        return "ServerCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (hostName != null ? "hostName=" + hostName + ", " : "") +
                (status != null ? "status=" + status + ", " : "") +
                (lastCheck != null ? "lastCheck=" + lastCheck + ", " : "") +
                (adminId != null ? "adminId=" + adminId + ", " : "") +
            "}";
    }

}
