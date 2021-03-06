/**
 * Copyright (C) 2013, 2014 Johannes Taelman
 *
 * This file is part of Axoloti.
 *
 * Axoloti is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Axoloti is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Axoloti. If not, see <http://www.gnu.org/licenses/>.
 */
package generatedobjects;

import axoloti.attributedefinition.AxoAttributeComboBox;
import axoloti.inlets.InletBool32Rising;
import axoloti.inlets.InletBool32RisingFalling;
import axoloti.inlets.InletFrac32;
import axoloti.inlets.InletFrac32Bipolar;
import axoloti.inlets.InletFrac32Buffer;
import axoloti.object.AxoObject;
import axoloti.outlets.OutletFrac32Bipolar;
import axoloti.outlets.OutletFrac32Pos;
import axoloti.outlets.OutletInt32Pos;
import axoloti.parameters.ParameterFrac32SMap;
import axoloti.parameters.ParameterFrac32SMapKDTimeExp;
import axoloti.parameters.ParameterFrac32SMapKLineTimeExp;
import axoloti.parameters.ParameterFrac32SMapKLineTimeExp2;
import axoloti.parameters.ParameterFrac32UMap;
import axoloti.parameters.ParameterFrac32UMapKDecayTime;
import axoloti.parameters.ParameterFrac32UMapKDecayTimeReverse;
import axoloti.parameters.ParameterFrac32UMapKLineTimeReverse;
import static generatedobjects.gentools.WriteAxoObject;

/**
 *
 * @author Johannes Taelman
 */
public class Env extends gentools {

    static void GenerateAll() {
        String catName;

        catName = "env";
        WriteAxoObject(catName, Create_envd_new());

        catName = "env_old";
        WriteAxoObject(catName, Create_envadsr());
        WriteAxoObject(catName, Create_envad());
        WriteAxoObject(catName, Create_envd());
        WriteAxoObject(catName, Create_envd2());
        WriteAxoObject(catName, Create_envhd());
        WriteAxoObject(catName, Create_envhd2());
        WriteAxoObject(catName, Create_envahd());
        WriteAxoObject(catName, Create_envahd2());
        WriteAxoObject(catName, CreateEnvFollower());

        WriteAxoObject(catName, Create_envdlin());
        WriteAxoObject(catName, Create_envhdlin());
        WriteAxoObject(catName, Create_envahdlin());

        WriteAxoObject(catName, Create_envdlinx());
        WriteAxoObject(catName, Create_envhdlinx());
        WriteAxoObject(catName, Create_envahdlinx());

        WriteAxoObject(catName, Create_envdlinmx());
        WriteAxoObject(catName, Create_envhdlinmx());
        WriteAxoObject(catName, Create_envahdlinmx());

        WriteAxoObject(catName, Create_line2());
        WriteAxoObject(catName, Create_line3());

        WriteAxoObject(catName, Create_line2x());
        WriteAxoObject(catName, Create_line3x());

        WriteAxoObject(catName, Create_line2mx());
        WriteAxoObject(catName, Create_line3mx());

        WriteAxoObject(catName, Create_line2bp());
        WriteAxoObject(catName, Create_line3bp());
    }

    static AxoObject Create_envd_new() {
        AxoObject o = new AxoObject("d", "decay envelope");
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.outlets.add(new OutletFrac32Pos("env", "envelope output"));
        o.params.add(new ParameterFrac32SMapKDTimeExp("d"));
        o.sLocalData = "int32_t val;\n"
                + "int ntrig;\n";
        o.sInitCode = "val = 0;\n"
                + "ntrig = 0;\n";
        o.sKRateCode = "   if ((%trig%>0) && !ntrig) { val =1<<27; ntrig=1;}\n"
                + "   else { if (!(%trig%>0)) ntrig=0; val = ___SMMUL(val, %d%)<<1;}\n"
                + "   %env% = val;\n";
        return o;
    }

    static AxoObject Create_envd() {
        AxoObject o = new AxoObject("d", "decay envelope");
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.outlets.add(new OutletFrac32Pos("env", "envelope output"));
        o.params.add(new ParameterFrac32UMapKDecayTimeReverse("d"));
        o.sLocalData = "int32_t val;\n"
                + "int ntrig;\n";
        o.sInitCode = "val = 0;\n"
                + "ntrig = 0;\n";
        o.sKRateCode = "   if ((%trig%>0) && !ntrig) { val =1<<27; ntrig=1;}\n"
                + "   else { if (!(%trig%>0)) ntrig=0; val -= ___SMMUL(val, %d%>>1);}\n"
                + "   %env% = val;\n";
        return o;
    }

    static AxoObject Create_envd2() {
        AxoObject o = new AxoObject("d m", "decay envelope with decay time modulation input");
        o.inlets.add(new InletFrac32("dm", "decay time modulation"));
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.outlets.add(new OutletFrac32Pos("env", "envelope output"));
        o.params.add(new ParameterFrac32UMapKDecayTimeReverse("d"));
        o.sLocalData = "int32_t val;\n"
                + "int ntrig;\n";
        o.sInitCode = "val = 0;\n"
                + "ntrig = 0;\n";
        o.sKRateCode = "   if ((%trig%>0) && !ntrig) { val =1<<27; ntrig=1;}\n"
                + "   else { if (!(%trig%>0)) ntrig=0; val -= ___SMMUL(val, (%d%+%dm%)>>1);}\n"
                + "   %env% = val;\n";
        return o;
    }

    static AxoObject Create_envhd() {
        AxoObject o = new AxoObject("hd", "hold/decay envelope");
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.outlets.add(new OutletFrac32Pos("env", "envelope output"));
        o.params.add(new ParameterFrac32UMapKDecayTimeReverse("d"));
        o.sLocalData = "int32_t val;\n";
        o.sInitCode = "val = 0;\n";
        o.sKRateCode = "   if (%trig%>0) val =1<<27;\n"
                + "   else val -= ___SMMUL(val, %d%>>1);\n"
                + "   %env% = val;\n";
        return o;
    }

    static AxoObject Create_envhd2() {
        AxoObject o = new AxoObject("hd m", "hold/decay envelope with decay time modulation input");
        o.inlets.add(new InletFrac32("dm", "decay time modulation"));
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.outlets.add(new OutletFrac32Pos("env", "envelope output"));
        o.params.add(new ParameterFrac32UMapKDecayTimeReverse("d"));
        o.sLocalData = "int32_t val;\n";
        o.sInitCode = "   val = 0;\n";
        o.sKRateCode = "   if (%trig%>0) val =1<<27;\n"
                + "   else val -= ___SMMUL(val, (%d%+%dm%)>>1);\n"
                + "   %env% = val;\n";
        return o;
    }

    static AxoObject Create_envahd() {
        AxoObject o = new AxoObject("ahd", "attack hold decay envelope");
        o.inlets.add(new InletBool32RisingFalling("gate", "gate"));
        o.outlets.add(new OutletFrac32Pos("env", "envelope output"));
        o.params.add(new ParameterFrac32UMapKDecayTime("a"));
        o.params.add(new ParameterFrac32UMapKDecayTime("d"));
        o.sLocalData = "int32_t val;\n";
        o.sInitCode = "   val = 0;\n";
        o.sKRateCode = "   if (%gate%>0) val = ___SMMLA((1<<27)-val,(1<<26)-(%a%>>1),val);\n"
                + "   else val = ___SMMLA(val, (-1<<26)+(%d%>>1),val);\n"
                + "   %env%= val;\n";
        return o;
    }

    static AxoObject Create_envahd2() {
        AxoObject o = new AxoObject("ahd m", "attack hold decay envelope with modulation inputs");
        o.inlets.add(new InletFrac32("am", "attack time modulation"));
        o.inlets.add(new InletFrac32("dm", "decay time modulation"));
        o.inlets.add(new InletBool32RisingFalling("gate", "gate"));
        o.outlets.add(new OutletFrac32Pos("env", "envelope output"));
        o.params.add(new ParameterFrac32UMapKDecayTime("a"));
        o.params.add(new ParameterFrac32UMapKDecayTime("d"));
        o.sLocalData = "int32_t val;\n";
        o.sInitCode = "   val = 0;\n";
        o.sKRateCode = "   if (%gate%>0) val = ___SMMLA((1<<27)-val,(1<<26)-(%a%>>1)-(%am%>>1),val);\n"
                + "   else val = ___SMMLA(val,(-1<<26)+(%d%>>1)+(%dm%>>1),val);\n"
                + "   %env%= val;\n";
        return o;
    }

    static AxoObject Create_envadsr() {
        AxoObject o = new AxoObject("adsr", "Attack/decay/sustain/release envelope");
        o.outlets.add(new OutletFrac32Pos("env", "envelope output"));
        o.inlets.add(new InletBool32RisingFalling("gate", "gate"));
        o.params.add(new ParameterFrac32SMapKLineTimeExp2("a"));
        o.params.add(new ParameterFrac32SMapKDTimeExp("d"));
        o.params.add(new ParameterFrac32UMap("s"));
        o.params.add(new ParameterFrac32SMapKDTimeExp("r"));
        o.sLocalData = "int8_t stage;\n"
                + "int ntrig;\n"
                + "int32_t val;\n";
        o.sInitCode = "stage = 0;\n"
                + "ntrig = 0;\n"
                + "val = 0;\n";
        o.sKRateCode = "if ((%gate%>0) && !ntrig) {\n"
                + "   stage = 1;\n"
                + "   ntrig = 1;\n"
                + "}\n"
                + "if (!(%gate%>0) && ntrig) {\n"
                + "   stage = 0;\n"
                + "   ntrig=0;\n"
                + "}\n"
                + "if (stage == 0){\n"
                + "   val = ___SMMUL(val,%r%)<<1;\n"
                + "} else if (stage == 1){\n"
                + "   val = val + %a%;\n"
                + "   if (val<0) {\n"
                + "      val =0x7FFFFFFF;\n"
                + "      stage = 2;\n"
                + "   }\n"
                + "} else if (stage == 2) {\n"
                + "   val = (%s%<<4) + (___SMMUL(val - (%s%<<4),%d%)<<1);\n"
                + "}\n"
                + "\n"
                + "%env% = val>>4;";
        return o;
    }

    static AxoObject Create_envad() {
        AxoObject o = new AxoObject("ad", "Attack/decay envelope");
        o.outlets.add(new OutletFrac32Pos("env", "envelope output"));
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.params.add(new ParameterFrac32SMapKLineTimeExp2("a"));
        o.params.add(new ParameterFrac32SMapKDTimeExp("d"));
        o.sLocalData = "int8_t stage;\n"
                + "int ntrig;\n"
                + "int32_t val;\n";
        o.sInitCode = "stage = 0;\n"
                + "ntrig = 0;\n"
                + "val = 0;\n";
        o.sKRateCode = "if ((%trig%>0) && !ntrig) {\n"
                + "   stage = 1;\n"
                + "   ntrig = 1;\n"
                + "}\n"
                + "if (!(%trig%>0) && ntrig) {\n"
                + "   stage = 0;\n"
                + "   ntrig=0;\n"
                + "}\n"
                + "if (stage == 0){\n"
                + "   val = ___SMMUL(val,%d%)<<1;\n"
                + "} else {\n"
                + "   val = val + %a%;\n"
                + "   if (val<0) {\n"
                + "      val =0x7FFFFFFF;\n"
                + "      stage = 0;\n"
                + "   }\n"
                + "}"
                + "%env% = val>>4;";
        return o;
    }

    static AxoObject Create_envdlin() {
        AxoObject o = new AxoObject("d lin", "decay envelope, linear ramp");
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.outlets.add(new OutletFrac32Pos("env", "envelope output"));
        o.params.add(new ParameterFrac32UMapKLineTimeReverse("d"));
        o.sLocalData = "int32_t val;\n"
                + "int ntrig;\n";
        o.sInitCode = "val = 0;\n"
                + "ntrig = 0;\n";
        o.sKRateCode = "if ((%trig% > 0) && !ntrig) {\n"
                + "  val = 1 << 27;\n"
                + "  ntrig = 1;\n"
                + "}\n"
                + "else {\n"
                + "  if (!(%trig% > 0))\n"
                + "    ntrig = 0;\n"
                + "  val -= %d% >> 7;\n"
                + "  if (val < 0)\n"
                + "    val = 0;\n"
                + "}\n"
                + "%env% = val;\n";
        return o;
    }

    static AxoObject Create_envdlinx() {
        AxoObject o = new AxoObject("d lin x", "decay envelope, linear ramp, extended range");
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.outlets.add(new OutletFrac32Pos("env", "envelope output"));
        o.params.add(new ParameterFrac32SMapKLineTimeExp("d"));
        o.sLocalData = "int32_t val;\n"
                + "int ntrig;\n";
        o.sInitCode = "val = 0;\n"
                + "ntrig = 0;\n";
        o.sKRateCode = "if ((%trig% > 0) && !ntrig) {\n"
                + "  val = 1 << 27;\n"
                + "  ntrig = 1;\n"
                + "}\n"
                + "else {\n"
                + "  if (!(%trig% > 0))\n"
                + "    ntrig = 0;\n"
                + "  int32_t t;\n"
                + "  MTOF(-%d%,t);\n"
                + "  val -= t>>6;\n"
                + "  if (val < 0)\n"
                + "    val = 0;\n"
                + "}\n"
                + "%env% = val;\n";
        return o;
    }

    static AxoObject Create_envdlinmx() {
        AxoObject o = new AxoObject("d lin m x", "decay envelope, linear ramp, modulation input, extended range");
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.inlets.add(new InletFrac32Bipolar("dm", "d modulation"));
        o.outlets.add(new OutletFrac32Pos("env", "envelope output"));
        o.params.add(new ParameterFrac32SMapKLineTimeExp("d"));
        o.sLocalData = "int32_t val;\n"
                + "int ntrig;\n";
        o.sInitCode = "val = 0;\n"
                + "ntrig = 0;\n";
        o.sKRateCode = "if ((%trig% > 0) && !ntrig) {\n"
                + "  val = 1 << 27;\n"
                + "  ntrig = 1;\n"
                + "}\n"
                + "else {\n"
                + "  if (!(%trig% > 0))\n"
                + "    ntrig = 0;\n"
                + "  int32_t t;\n"
                + "  int32_t dt = %d% + %dm%;\n"
                + "  MTOF(-dt,t);\n"
                + "  val -= t>>6;\n"
                + "  if (val < 0)\n"
                + "    val = 0;\n"
                + "}\n"
                + "%env% = val;\n";
        return o;
    }

    static AxoObject Create_envhdlin() {
        AxoObject o = new AxoObject("hd lin", "hold/decay envelope, linear ramp");
        o.inlets.add(new InletBool32RisingFalling("trig", "trigger"));
        o.outlets.add(new OutletFrac32Pos("env", "envelope output"));
        o.params.add(new ParameterFrac32UMapKLineTimeReverse("d"));
        o.sLocalData = "int32_t val;\n";
        o.sInitCode = "val = 0;\n";
        o.sKRateCode = "if (%trig% > 0) {\n"
                + "  val = 1 << 27;\n"
                + "}\n"
                + "else {\n"
                + "  val -= %d% >> 7;\n"
                + "  if (val < 0)\n"
                + "    val = 0;\n"
                + "}\n"
                + "%env% = val;\n";
        return o;
    }

    static AxoObject Create_envhdlinx() {
        AxoObject o = new AxoObject("hd lin x", "hold/decay envelope, linear ramp, extended range");
        o.inlets.add(new InletBool32RisingFalling("trig", "trigger"));
        o.outlets.add(new OutletFrac32Pos("env", "envelope output"));
        o.params.add(new ParameterFrac32SMapKLineTimeExp("d"));
        o.sLocalData = "int32_t val;\n";
        o.sInitCode = "val = 0;\n";
        o.sKRateCode = "if (%trig% > 0) {\n"
                + "  val = 1 << 27;\n"
                + "}\n"
                + "else {\n"
                + "  int32_t t;\n"
                + "  MTOF(-%d%,t);\n"
                + "  val -= t>>6;\n"
                + "  if (val < 0)\n"
                + "    val = 0;\n"
                + "}\n"
                + "%env% = val;\n";
        return o;
    }

    static AxoObject Create_envhdlinmx() {
        AxoObject o = new AxoObject("hd lin m x", "hold/decay envelope, linear ramp, modulation input extended range");
        o.inlets.add(new InletBool32RisingFalling("trig", "trigger"));
        o.inlets.add(new InletFrac32Bipolar("dm", "d modulation"));
        o.outlets.add(new OutletFrac32Pos("env", "envelope output"));
        o.params.add(new ParameterFrac32SMapKLineTimeExp("d"));
        o.sLocalData = "int32_t val;\n";
        o.sInitCode = "val = 0;\n";
        o.sKRateCode = "if (%trig% > 0) {\n"
                + "  val = 1 << 27;\n"
                + "}\n"
                + "else {\n"
                + "  int32_t t;\n"
                + "  int32_t dt = %d% + %dm%;\n"
                + "  MTOF(-dt,t);\n"
                + "  val -= t>>6;\n"
                + "  if (val < 0)\n"
                + "    val = 0;\n"
                + "}\n"
                + "%env% = val;\n";
        return o;
    }

    static AxoObject Create_envahdlin() {
        AxoObject o = new AxoObject("ahd lin", "attack/hold/decay envelope, linear ramps");
        o.inlets.add(new InletBool32RisingFalling("trig", "trigger"));
        o.outlets.add(new OutletFrac32Pos("env", "envelope output"));
        o.params.add(new ParameterFrac32UMapKLineTimeReverse("a"));
        o.params.add(new ParameterFrac32UMapKLineTimeReverse("d"));
        o.sLocalData = "int32_t val;\n";
        o.sInitCode = "val = 0;\n";
        o.sKRateCode = "if (%trig% > 0) {\n"
                + "  val += %a% >> 7;\n"
                + "}\n"
                + "else {\n"
                + "  val -= %d% >> 7;\n"
                + "}\n"
                + "val = __USAT(val,27);\n"
                + "%env% = val;\n";
        return o;
    }

    static AxoObject Create_envahdlinx() {
        AxoObject o = new AxoObject("ahd lin x", "attack/hold/decay envelope, linear ramps, extended range");
        o.inlets.add(new InletBool32RisingFalling("trig", "trigger"));
        o.outlets.add(new OutletFrac32Pos("env", "envelope output"));
        o.params.add(new ParameterFrac32SMapKLineTimeExp("a"));
        o.params.add(new ParameterFrac32SMapKLineTimeExp("d"));
        o.sLocalData = "int32_t val;\n";
        o.sInitCode = "val = 0;\n";
        o.sKRateCode = "if (%trig% > 0) {\n"
                + "  int32_t t;\n"
                + "  MTOF(-%a%,t);\n"
                + "  val += t>>6;\n"
                + "}\n"
                + "else {\n"
                + "  int32_t t;\n"
                + "  MTOF(-%d%,t);\n"
                + "  val -= t>>6;\n"
                + "}\n"
                + "val = __USAT(val,27);\n"
                + "%env% = val;\n";
        return o;
    }

    static AxoObject Create_envahdlinmx() {
        AxoObject o = new AxoObject("ahd lin m x", "attack/hold/decay envelope, linear ramps, modulation inputs, extended range");
        o.inlets.add(new InletBool32RisingFalling("trig", "trigger"));
        o.inlets.add(new InletFrac32Bipolar("am", "attack time modulation"));
        o.inlets.add(new InletFrac32Bipolar("dm", "decay time modulation"));
        o.outlets.add(new OutletFrac32Pos("env", "envelope output"));
        o.params.add(new ParameterFrac32SMapKLineTimeExp("a"));
        o.params.add(new ParameterFrac32SMapKLineTimeExp("d"));
        o.sLocalData = "int32_t val;\n";
        o.sInitCode = "val = 0;\n";
        o.sKRateCode = "if (%trig% > 0) {\n"
                + "  int32_t t;\n"
                + "  int32_t at = %a% + %am%;\n"
                + "  MTOF(-at,t);\n"
                + "  val += t>>6;\n"
                + "}\n"
                + "else {\n"
                + "  int32_t t;\n"
                + "  int32_t dt = %d% + %dm%;\n"
                + "  MTOF(-dt,t);\n"
                + "  val -= t>>6;\n"
                + "}\n"
                + "val = __USAT(val,27);\n"
                + "%env% = val;\n";
        return o;
    }

    static AxoObject Create_line2() {
        AxoObject o = new AxoObject("line 2", "two piecewise linear ramps");
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.outlets.add(new OutletFrac32Pos("out", "output"));
        o.outlets.add(new OutletInt32Pos("phase", "phase index"));
        o.params.add(new ParameterFrac32UMap("v0"));
        o.params.add(new ParameterFrac32UMapKLineTimeReverse("tA"));
        o.params.add(new ParameterFrac32UMap("v1"));
        o.params.add(new ParameterFrac32UMapKLineTimeReverse("tB"));
        o.params.add(new ParameterFrac32UMap("v2"));
        o.sLocalData = "int32_t val;\n"
                + "int32_t time1;\n"
                + "int32_t phase1;\n"
                + "int32_t ntrig;\n";
        o.sInitCode = "phase1 = 0;\n"
                + "val = %v2%;\n"
                + "ntrig = 0;\n";
        o.sKRateCode = "if ((%trig% > 0) && (!ntrig)) {\n"
                + "  time1 = 0;\n"
                + "  phase1 = 1;\n"
                + "  val = %v0%;\n"
                + "  ntrig = 1;\n"
                + "}\n"
                + "else if (phase1 == 1) {\n"
                + "  time1 += %tA%>>3;\n"
                + "  if (time1>=0)\n"
                + "     val = %v0% + (___SMMUL(%v1%-%v0%,time1)<<1);\n"
                + "  else {\n"
                + "    phase1 = 2;\n"
                + "    val = %v1%;\n"
                + "    time1 = 0;\n"
                + "  }\n"
                + "}\n"
                + "else if (phase1 == 2) {\n"
                + "  time1 += %tB%>>3;\n"
                + "  if (time1>=0)\n"
                + "     val = %v1% + (___SMMUL(%v2%-%v1%,time1)<<1);\n"
                + "  else {\n"
                + "    phase1 = 0;\n"
                + "    val = %v2%;\n"
                + "  }\n"
                + "}\n"
                + "if (!(%trig% > 0)) ntrig = 0;\n"
                + "%out% = val;\n"
                + "%phase% = phase1;\n";
        return o;
    }

    static AxoObject Create_line2x() {
        AxoObject o = new AxoObject("line 2 x", "two piecewise linear ramps, extended range");
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.outlets.add(new OutletFrac32Pos("out", "output"));
        o.outlets.add(new OutletInt32Pos("phase", "phase index"));
        o.params.add(new ParameterFrac32UMap("v0"));
        o.params.add(new ParameterFrac32SMapKLineTimeExp("tA"));
        o.params.add(new ParameterFrac32UMap("v1"));
        o.params.add(new ParameterFrac32SMapKLineTimeExp("tB"));
        o.params.add(new ParameterFrac32UMap("v2"));
        o.sLocalData = "int32_t val;\n"
                + "int32_t time1;\n"
                + "int32_t phase1;\n"
                + "int32_t ntrig;\n";
        o.sInitCode = "phase1 = 0;\n"
                + "val = %v2%;\n"
                + "ntrig = 0;\n";
        o.sKRateCode = "if ((%trig% > 0) && (!ntrig)) {\n"
                + "  time1 = 0;\n"
                + "  phase1 = 1;\n"
                + "  val = %v0%;\n"
                + "  ntrig = 1;\n"
                + "}\n"
                + "else if (phase1 == 1) {\n"
                + "  int32_t t;\n"
                + "  MTOF(-%tA%,t);\n"
                + "  time1 += t>>2;\n"
                + "  if (time1>=0)\n"
                + "     val = %v0% + (___SMMUL(%v1%-%v0%,time1)<<1);\n"
                + "  else {\n"
                + "    phase1 = 2;\n"
                + "    val = %v1%;\n"
                + "    time1 = 0;\n"
                + "  }\n"
                + "}\n"
                + "else if (phase1 == 2) {\n"
                + "  int32_t t;\n"
                + "  MTOF(-%tB%,t);\n"
                + "  time1 += t>>2;\n"
                + "  if (time1>=0)\n"
                + "     val = %v1% + (___SMMUL(%v2%-%v1%,time1)<<1);\n"
                + "  else {\n"
                + "    phase1 = 0;\n"
                + "    val = %v2%;\n"
                + "  }\n"
                + "}\n"
                + "if (!(%trig% > 0)) ntrig = 0;\n"
                + "%out% = val;\n"
                + "%phase% = phase1;\n";
        return o;
    }

    static AxoObject Create_line2mx() {
        AxoObject o = new AxoObject("line 2 m x", "two piecewise linear ramps, extended range, time modulation inputs");
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.inlets.add(new InletFrac32Bipolar("tAm", "time A (v0..v1) modulation"));
        o.inlets.add(new InletFrac32Bipolar("tBm", "time B (v1..v2) modulation"));
        o.outlets.add(new OutletFrac32Pos("out", "output"));
        o.outlets.add(new OutletInt32Pos("phase", "phase index"));
        o.params.add(new ParameterFrac32UMap("v0"));
        o.params.add(new ParameterFrac32SMapKLineTimeExp("tA"));
        o.params.add(new ParameterFrac32UMap("v1"));
        o.params.add(new ParameterFrac32SMapKLineTimeExp("tB"));
        o.params.add(new ParameterFrac32UMap("v2"));
        o.sLocalData = "int32_t val;\n"
                + "int32_t time1;\n"
                + "int32_t phase1;\n"
                + "int32_t ntrig;\n";
        o.sInitCode = "phase1 = 0;\n"
                + "val = %v2%;\n"
                + "ntrig = 0;\n";
        o.sKRateCode = "if ((%trig% > 0) && (!ntrig)) {\n"
                + "  time1 = 0;\n"
                + "  phase1 = 1;\n"
                + "  val = %v0%;\n"
                + "  ntrig = 1;\n"
                + "}\n"
                + "else if (phase1 == 1) {\n"
                + "  int32_t t;\n"
                + "  int32_t tA2 = %tA% + %tAm%;\n"
                + "  MTOF(-tA2,t);\n"
                + "  time1 += t>>2;\n"
                + "  if (time1>=0)\n"
                + "     val = %v0% + (___SMMUL(%v1%-%v0%,time1)<<1);\n"
                + "  else {\n"
                + "    phase1 = 2;\n"
                + "    val = %v1%;\n"
                + "    time1 = 0;\n"
                + "  }\n"
                + "}\n"
                + "else if (phase1 == 2) {\n"
                + "  int32_t t;\n"
                + "  int32_t tB2 = %tB% + %tBm%;\n"
                + "  MTOF(-tB2,t);\n"
                + "  time1 += t>>2;\n"
                + "  if (time1>=0)\n"
                + "     val = %v1% + (___SMMUL(%v2%-%v1%,time1)<<1);\n"
                + "  else {\n"
                + "    phase1 = 0;\n"
                + "    val = %v2%;\n"
                + "  }\n"
                + "}\n"
                + "if (!(%trig% > 0)) ntrig = 0;\n"
                + "%out% = val;\n"
                + "%phase% = phase1;\n";
        return o;
    }

    static AxoObject Create_line3() {
        AxoObject o = new AxoObject("line 3", "Three piecewise linear ramps");
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.outlets.add(new OutletFrac32Pos("out", "output"));
        o.outlets.add(new OutletInt32Pos("phase", "phase index"));
        o.params.add(new ParameterFrac32UMap("v0"));
        o.params.add(new ParameterFrac32UMapKLineTimeReverse("tA"));
        o.params.add(new ParameterFrac32UMap("v1"));
        o.params.add(new ParameterFrac32UMapKLineTimeReverse("tB"));
        o.params.add(new ParameterFrac32UMap("v2"));
        o.params.add(new ParameterFrac32UMapKLineTimeReverse("tC"));
        o.params.add(new ParameterFrac32UMap("v3"));
        o.sLocalData = "int32_t val;\n"
                + "int32_t time1;\n"
                + "int32_t phase1;\n"
                + "int32_t ntrig;\n";
        o.sInitCode = "phase1 = 0;\n"
                + "val = %v3%;\n"
                + "ntrig = 0;\n";
        o.sKRateCode = "if ((%trig% > 0) && (!ntrig)) {\n"
                + "  time1 = 0;\n"
                + "  phase1 = 1;\n"
                + "  val = %v0%;\n"
                + "  ntrig = 1;\n"
                + "}\n"
                + "else if (phase1 == 1) {\n"
                + "  time1 += %tA%>>3;\n"
                + "  if (time1>=0)\n"
                + "     val = %v0% + (___SMMUL(%v1%-%v0%,time1)<<1);\n"
                + "  else {\n"
                + "    phase1 = 2;\n"
                + "    val = %v1%;\n"
                + "    time1 = 0;\n"
                + "  }\n"
                + "}\n"
                + "else if (phase1 == 2) {\n"
                + "  time1 += %tB%>>3;\n"
                + "  if (time1>=0)\n"
                + "     val = %v1% + (___SMMUL(%v2%-%v1%,time1)<<1);\n"
                + "  else {\n"
                + "    phase1 = 3;\n"
                + "    val = %v2%;\n"
                + "    time1 = 0;\n"
                + "  }\n"
                + "}\n"
                + "else if (phase1 == 3) {\n"
                + "  time1 += %tC%>>3;\n"
                + "  if (time1>=0)\n"
                + "     val = %v2% + (___SMMUL(%v3%-%v2%,time1)<<1);\n"
                + "  else {\n"
                + "    phase1 = 0;\n"
                + "    val = %v3%;\n"
                + "  }\n"
                + "}\n"
                + "if (!(%trig% > 0)) ntrig = 0;\n"
                + "%out% = val;\n"
                + "%phase% = phase1;\n";
        return o;
    }

    static AxoObject Create_line3x() {
        AxoObject o = new AxoObject("line 3 x", "Three piecewise linear ramps, extended range");
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.outlets.add(new OutletFrac32Pos("out", "output"));
        o.outlets.add(new OutletInt32Pos("phase", "phase index"));
        o.params.add(new ParameterFrac32UMap("v0"));
        o.params.add(new ParameterFrac32SMapKLineTimeExp("tA"));
        o.params.add(new ParameterFrac32UMap("v1"));
        o.params.add(new ParameterFrac32SMapKLineTimeExp("tB"));
        o.params.add(new ParameterFrac32UMap("v2"));
        o.params.add(new ParameterFrac32SMapKLineTimeExp("tC"));
        o.params.add(new ParameterFrac32UMap("v3"));
        o.sLocalData = "int32_t val;\n"
                + "int32_t time1;\n"
                + "int32_t phase1;\n"
                + "int32_t ntrig;\n";
        o.sInitCode = "phase1 = 0;\n"
                + "val = %v3%;\n"
                + "ntrig = 0;\n";
        o.sKRateCode = "if ((%trig% > 0) && (!ntrig)) {\n"
                + "  time1 = 0;\n"
                + "  phase1 = 1;\n"
                + "  val = %v0%;\n"
                + "  ntrig = 1;\n"
                + "}\n"
                + "else if (phase1 == 1) {\n"
                + "  int32_t t;\n"
                + "  MTOF(-%tA%,t);\n"
                + "  time1 += t>>2;\n"
                + "  if (time1>=0)\n"
                + "     val = %v0% + (___SMMUL(%v1%-%v0%,time1)<<1);\n"
                + "  else {\n"
                + "    phase1 = 2;\n"
                + "    val = %v1%;\n"
                + "    time1 = 0;\n"
                + "  }\n"
                + "}\n"
                + "else if (phase1 == 2) {\n"
                + "  int32_t t;\n"
                + "  MTOF(-%tB%,t);\n"
                + "  time1 += t>>2;\n"
                + "  if (time1>=0)\n"
                + "     val = %v1% + (___SMMUL(%v2%-%v1%,time1)<<1);\n"
                + "  else {\n"
                + "    phase1 = 3;\n"
                + "    val = %v2%;\n"
                + "    time1 = 0;\n"
                + "  }\n"
                + "}\n"
                + "else if (phase1 == 3) {\n"
                + "  int32_t t;\n"
                + "  MTOF(-%tC%,t);\n"
                + "  time1 += t>>2;\n"
                + "  if (time1>=0)\n"
                + "     val = %v2% + (___SMMUL(%v3%-%v2%,time1)<<1);\n"
                + "  else {\n"
                + "    phase1 = 0;\n"
                + "    val = %v3%;\n"
                + "  }\n"
                + "}\n"
                + "if (!(%trig% > 0)) ntrig = 0;\n"
                + "%out% = val;\n"
                + "%phase% = phase1;\n";
        return o;
    }

    static AxoObject Create_line3mx() {
        AxoObject o = new AxoObject("line 3 m x", "Three piecewise linear ramps, extended range, time modulation inputs");
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.inlets.add(new InletFrac32Bipolar("tAm", "time A (v0..v1) modulation"));
        o.inlets.add(new InletFrac32Bipolar("tBm", "time B (v1..v2) modulation"));
        o.inlets.add(new InletFrac32Bipolar("tCm", "time C (v2..v3) modulation"));
        o.outlets.add(new OutletFrac32Pos("out", "output"));
        o.outlets.add(new OutletInt32Pos("phase", "phase index"));
        o.params.add(new ParameterFrac32UMap("v0"));
        o.params.add(new ParameterFrac32SMapKLineTimeExp("tA"));
        o.params.add(new ParameterFrac32UMap("v1"));
        o.params.add(new ParameterFrac32SMapKLineTimeExp("tB"));
        o.params.add(new ParameterFrac32UMap("v2"));
        o.params.add(new ParameterFrac32SMapKLineTimeExp("tC"));
        o.params.add(new ParameterFrac32UMap("v3"));
        o.sLocalData = "int32_t val;\n"
                + "int32_t time1;\n"
                + "int32_t phase1;\n"
                + "int32_t ntrig;\n";
        o.sInitCode = "phase1 = 0;\n"
                + "val = %v3%;\n"
                + "ntrig = 0;\n";
        o.sKRateCode = "if ((%trig% > 0) && (!ntrig)) {\n"
                + "  time1 = 0;\n"
                + "  phase1 = 1;\n"
                + "  val = %v0%;\n"
                + "  ntrig = 1;\n"
                + "}\n"
                + "else if (phase1 == 1) {\n"
                + "  int32_t t;\n"
                + "  int32_t tA2 = %tA% + %tAm%;\n"
                + "  MTOF(-tA2,t);\n"
                + "  time1 += t>>2;\n"
                + "  if (time1>=0)\n"
                + "     val = %v0% + (___SMMUL(%v1%-%v0%,time1)<<1);\n"
                + "  else {\n"
                + "    phase1 = 2;\n"
                + "    val = %v1%;\n"
                + "    time1 = 0;\n"
                + "  }\n"
                + "}\n"
                + "else if (phase1 == 2) {\n"
                + "  int32_t t;\n"
                + "  int32_t tB2 = %tB% + %tBm%;\n"
                + "  MTOF(-tB2,t);\n"
                + "  time1 += t>>2;\n"
                + "  if (time1>=0)\n"
                + "     val = %v1% + (___SMMUL(%v2%-%v1%,time1)<<1);\n"
                + "  else {\n"
                + "    phase1 = 3;\n"
                + "    val = %v2%;\n"
                + "    time1 = 0;\n"
                + "  }\n"
                + "}\n"
                + "else if (phase1 == 3) {\n"
                + "  int32_t t;\n"
                + "  int32_t tC2 = %tC% + %tCm%;\n"
                + "  MTOF(-tC2,t);\n"
                + "  time1 += t>>2;\n"
                + "  if (time1>=0)\n"
                + "     val = %v2% + (___SMMUL(%v3%-%v2%,time1)<<1);\n"
                + "  else {\n"
                + "    phase1 = 0;\n"
                + "    val = %v3%;\n"
                + "  }\n"
                + "}\n"
                + "if (!(%trig% > 0)) ntrig = 0;\n"
                + "%out% = val;\n"
                + "%phase% = phase1;\n";
        return o;
    }

    static AxoObject Create_line2bp() {
        AxoObject o = new AxoObject("line 2 bp", "two piecewise linear ramps, bipolar version");
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.outlets.add(new OutletFrac32Bipolar("out", "output"));
        o.outlets.add(new OutletInt32Pos("phase", "phase index"));
        o.params.add(new ParameterFrac32SMap("v0"));
        o.params.add(new ParameterFrac32UMapKLineTimeReverse("tA"));
        o.params.add(new ParameterFrac32SMap("v1"));
        o.params.add(new ParameterFrac32UMapKLineTimeReverse("tB"));
        o.params.add(new ParameterFrac32SMap("v2"));
        o.sLocalData = "int32_t val;\n"
                + "int32_t time1;\n"
                + "int32_t phase1;\n"
                + "int32_t ntrig;\n";
        o.sInitCode = "phase1 = 0;\n"
                + "val = %v2%;\n"
                + "ntrig = 0;\n";
        o.sKRateCode = "if ((%trig% > 0) && (!ntrig)) {\n"
                + "  time1 = 0;\n"
                + "  phase1 = 1;\n"
                + "  val = %v0%;\n"
                + "  ntrig = 1;\n"
                + "}\n"
                + "else if (phase1 == 1) {\n"
                + "  time1 += %tA%>>3;\n"
                + "  if (time1>=0)\n"
                + "     val = %v0% + (___SMMUL(%v1%-%v0%,time1)<<1);\n"
                + "  else {\n"
                + "    phase1 = 2;\n"
                + "    val = %v1%;\n"
                + "    time1 = 0;\n"
                + "  }\n"
                + "}\n"
                + "else if (phase1 == 2) {\n"
                + "  time1 += %tB%>>3;\n"
                + "  if (time1>=0)\n"
                + "     val = %v1% + (___SMMUL(%v2%-%v1%,time1)<<1);\n"
                + "  else {\n"
                + "    phase1 = 0;\n"
                + "    val = %v2%;\n"
                + "  }\n"
                + "}\n"
                + "if (!(%trig% > 0)) ntrig = 0;\n"
                + "%out% = val;\n"
                + "%phase% = phase1;\n";
        return o;
    }

    static AxoObject Create_line3bp() {
        AxoObject o = new AxoObject("line 3 bp", "Three piecewise linear ramps, bipolar version");
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.outlets.add(new OutletFrac32Bipolar("out", "output"));
        o.outlets.add(new OutletInt32Pos("phase", "phase index"));
        o.params.add(new ParameterFrac32SMap("v0"));
        o.params.add(new ParameterFrac32UMapKLineTimeReverse("tA"));
        o.params.add(new ParameterFrac32SMap("v1"));
        o.params.add(new ParameterFrac32UMapKLineTimeReverse("tB"));
        o.params.add(new ParameterFrac32SMap("v2"));
        o.params.add(new ParameterFrac32UMapKLineTimeReverse("tC"));
        o.params.add(new ParameterFrac32SMap("v3"));
        o.sLocalData = "int32_t val;\n"
                + "int32_t time1;\n"
                + "int32_t phase1;\n"
                + "int32_t ntrig;\n";
        o.sInitCode = "phase1 = 0;\n"
                + "val = %v3%;\n"
                + "ntrig = 0;\n";
        o.sKRateCode = "if ((%trig% > 0) && (!ntrig)) {\n"
                + "  time1 = 0;\n"
                + "  phase1 = 1;\n"
                + "  val = %v0%;\n"
                + "  ntrig = 1;\n"
                + "}\n"
                + "else if (phase1 == 1) {\n"
                + "  time1 += %tA%>>3;\n"
                + "  if (time1>=0)\n"
                + "     val = %v0% + (___SMMUL(%v1%-%v0%,time1)<<1);\n"
                + "  else {\n"
                + "    phase1 = 2;\n"
                + "    val = %v1%;\n"
                + "    time1 = 0;\n"
                + "  }\n"
                + "}\n"
                + "else if (phase1 == 2) {\n"
                + "  time1 += %tB%>>3;\n"
                + "  if (time1>=0)\n"
                + "     val = %v1% + (___SMMUL(%v2%-%v1%,time1)<<1);\n"
                + "  else {\n"
                + "    phase1 = 3;\n"
                + "    val = %v2%;\n"
                + "    time1 = 0;\n"
                + "  }\n"
                + "}\n"
                + "else if (phase1 == 3) {\n"
                + "  time1 += %tC%>>3;\n"
                + "  if (time1>=0)\n"
                + "     val = %v2% + (___SMMUL(%v3%-%v2%,time1)<<1);\n"
                + "  else {\n"
                + "    phase1 = 0;\n"
                + "    val = %v3%;\n"
                + "  }\n"
                + "}\n"
                + "if (!(%trig% > 0)) ntrig = 0;\n"
                + "%out% = val;\n"
                + "%phase% = phase1;\n";
        return o;
    }

    static AxoObject CreateEnvFollower() {
        AxoObject o = new AxoObject("follower", "envelope follower, linear output");
        o.outlets.add(new OutletFrac32Pos("amp", "amplitude"));
        o.inlets.add(new InletFrac32Buffer("in", "input wave"));
        String mentries[] = {"1.3ms", "2.7ms", "5.3ms", "10.6ms", "21.2ms", "42.6ms", "85.3ms", "170.6ms"};
        String centries[] = {"2", "3", "4", "5", "6", "7", "8", "9"};
        o.attributes.add(new AxoAttributeComboBox("time", mentries, centries));
        o.sLocalData = "int32_t accu;\n";
        o.sInitCode = "accu = 0;\n";
        o.sKRateCode = " int sabs = 0;\n"
                + "int i;"
                + "  for(i=0;i<BUFSIZE;i++){"
                + "    int32_t v = %in%[i];\n"
                + "    sabs += v>0?v:-v;\n"
                + "  }\n"
                + "  accu -= accu>>%time%;\n"
                + "  accu += sabs>>(%time%+4);\n"
                + "  %amp% = accu;\n";
        return o;
    }
}
