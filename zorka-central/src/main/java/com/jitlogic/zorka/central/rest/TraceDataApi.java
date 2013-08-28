/**
 * Copyright 2012-2013 Rafal Lewczuk <rafal.lewczuk@jitlogic.com>
 * <p/>
 * This is free software. You can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * <p/>
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this software. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jitlogic.zorka.central.rest;

import com.jitlogic.zorka.central.*;
import com.jitlogic.zorka.central.data.*;
import com.jitlogic.zorka.central.rds.RDSStore;
import com.jitlogic.zorka.common.tracedata.*;
import com.jitlogic.zorka.common.util.ZorkaUtil;
import org.fressian.FressianReader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;

@Path("hosts")
public class TraceDataApi {

    private HostStoreManager storeManager;


    public TraceDataApi() {
        storeManager = CentralApp.getInstance().getStoreManager();
    }


    public TraceDataApi(HostStoreManager storeManager) {
        this.storeManager = storeManager;
    }


    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public List<HostInfo> getHosts() {
        List<HostInfo> infos = new ArrayList<HostInfo>();
        for (HostStore host : storeManager.list()) {
            infos.add(host.getHostInfo());
        }
        return infos;
    }


    @GET
    @Path("/{hostId: [0-9]+}/{traceId: [0-9]+}")
    @Produces(MediaType.APPLICATION_JSON)
    public TraceInfo getTrace(@PathParam("hostId") int hostId, @PathParam("traceId") long traceOffs) {

        return storeManager.getHost(hostId).getTrace(traceOffs);
    }


    @POST
    @Path("/{hostId: [0-9]+}/page")
    @Consumes(MediaType.APPLICATION_JSON)
    public PagingData<TraceInfo> pageTraces(@PathParam("hostId") int hostId,
                                            @DefaultValue("0") @QueryParam("offset") int offset,
                                            @DefaultValue("100") @QueryParam("limit") int limit,
                                            TraceListFilterExpression filter) {

        return storeManager.getHost(hostId).pageTraces(offset, limit, filter);
    }


    @GET
    @Path("/{hostId: [0-9]+}/{traceOffs: [0-9]+}/get")
    @Produces(MediaType.APPLICATION_JSON)
    public TraceRecordInfo getRecord(
            @PathParam("hostId") int hostId,
            @PathParam("traceOffs") long traceOffs,
            @DefaultValue("") @QueryParam("path") String path) {

        TraceContext ctx = storeManager.getHost(hostId).getTraceContext(traceOffs);
        return ctx.packTraceRecord(ctx.getTraceRecord(path), path);
    }


    @GET
    @Path("/{hostId: [0-9]+}/{traceOffs: [0-9]+}/list")
    @Produces(MediaType.APPLICATION_JSON)
    public List<TraceRecordInfo> listRecords(
            @PathParam("hostId") int hostId,
            @PathParam("traceOffs") long traceOffs,
            @DefaultValue("") @QueryParam("path") String path) {

        TraceContext ctx = storeManager.getHost(hostId).getTraceContext(traceOffs);
        TraceRecord tr = ctx.getTraceRecord(path);

        List<TraceRecordInfo> lst = new ArrayList<TraceRecordInfo>();

        for (int i = 0; i < tr.numChildren(); i++) {
            lst.add(ctx.packTraceRecord(tr.getChild(i), path.length() > 0 ? (path + "/" + i) : "" + i));
        }

        return lst;
    }


    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public void addHost(HostInfo hostInfo) {
        HostStore store = storeManager.getOrCreateHost(hostInfo.getName(), hostInfo.getAddr());
        store.updateInfo(hostInfo);
        store.save();
    }


    @PUT
    @Path("/{hostId: [0-9]+}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateHost(@PathParam("hostId") int hostId, HostInfo info) {
        HostStore store = storeManager.getHost(hostId);
        store.updateInfo(info);
        store.save();
    }


    @DELETE
    @Path("/{hostId: [0-9]+}")
    public void deleteHost(@PathParam("hostId") int hostId) throws IOException {
        storeManager.delete(hostId);
    }
}