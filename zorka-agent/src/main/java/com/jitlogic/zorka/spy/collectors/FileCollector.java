/**
 * Copyright 2012 Rafal Lewczuk <rafal.lewczuk@jitlogic.com>
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
package com.jitlogic.zorka.spy.collectors;

import com.jitlogic.zorka.logproc.FileTrapper;
import com.jitlogic.zorka.spy.SpyProcessor;
import com.jitlogic.zorka.spy.SpyRecord;
import com.jitlogic.zorka.util.ObjectInspector;
import com.jitlogic.zorka.logproc.ZorkaLogLevel;

public class FileCollector implements SpyProcessor {

    private FileTrapper trapper;
    private String expr;
    private ZorkaLogLevel logLevel;
    private String tag;

    public FileCollector(FileTrapper trapper, String expr, ZorkaLogLevel logLevel, String tag) {
        this.trapper = trapper;
        this.expr = expr;
        this.logLevel = logLevel;
        this.tag = tag;
    }

    public SpyRecord process(SpyRecord record) {

        String msg = ObjectInspector.substitute(expr, record);

        trapper.log(logLevel, tag, msg, null);

        return record;
    }

}
