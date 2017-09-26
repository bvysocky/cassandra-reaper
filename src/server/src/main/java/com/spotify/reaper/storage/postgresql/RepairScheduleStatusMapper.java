/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.spotify.reaper.storage.postgresql;


import com.spotify.reaper.core.RepairSchedule;
import com.spotify.reaper.resources.view.RepairScheduleStatus;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.google.common.collect.ImmutableSet;
import org.apache.cassandra.repair.RepairParallelism;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public final class RepairScheduleStatusMapper implements ResultSetMapper<RepairScheduleStatus> {

  @Override
  public RepairScheduleStatus map(int index, ResultSet rs, StatementContext ctx) throws SQLException {

    return new RepairScheduleStatus(
        UuidUtil.fromSequenceId(rs.getLong("id")),
        rs.getString("owner"),
        rs.getString("cluster_name"),
        rs.getString("keyspace_name"),
        ImmutableSet.copyOf((String[]) rs.getArray("column_families").getArray()),
        RepairSchedule.State.valueOf(rs.getString("state")),
        RepairRunMapper.getDateTimeOrNull(rs, "creation_time"),
        RepairRunMapper.getDateTimeOrNull(rs, "next_activation"),
        RepairRunMapper.getDateTimeOrNull(rs, "pause_time"),
        rs.getDouble("intensity"),
        rs.getBoolean("incremental_repair"),
        rs.getInt("segment_count"),
        RepairParallelism.fromName(
            rs.getString("repair_parallelism").toLowerCase().replace("datacenter_aware", "dc_parallel")),
        rs.getInt("days_between"),
        ImmutableSet.copyOf((String[]) rs.getArray("nodes").getArray()),
        ImmutableSet.copyOf((String[]) rs.getArray("datacenters").getArray()));
  }
}
