package org.apache.spark.sql

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.spark.internal.Logging
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.catalyst.expressions.codegen.GeneratePredicate._
import org.apache.spark.sql.catalyst.expressions.codegen.{Predicate, _}
import org.apache.spark.sql.catalyst.expressions.{Attribute, BindReferences, Expression, _}

/**
  * Interface for generated predicate
  */
abstract class Predicate {
  def eval(r: InternalRow): Boolean
}

/**
  * Generates bytecode that evaluates a boolean [[Expression]] on a given input [[InternalRow]].
  */
//object generateJCUDA extends CodeGenerator[Expression, (InternalRow) => Boolean] {
  object generateJCUDA extends Logging {


    def generate(predicate: Expression, inputSchema: Seq[Attribute]): (InternalRow) => Boolean = {
    val ctx = newCodeGenContext()
    // val eval = predicate.genCode(ctx)
    val codeBody = s"""
      // This whole java code will be typcasted to org.apache.spark.sql.catalyst.expressions.GeneratedClass
      // which will have a generate() function should supply the implemented java class.

      public JCUDAClass generate(Object[] references) {
        return new JCUDAClass(references);
      }

      class JCUDAClass extends ${classOf[Predicate].getName} {
        private final Object[] references;
        ${ctx.declareMutableStates()}
        ${ctx.declareAddedFunctions()}

        public JCUDAClass(Object[] references) {
          this.references = references;
          ${ctx.initMutableStates()}
        }

        public boolean eval(InternalRow ${ctx.INPUT_ROW}) {
          boolean isNull = true;
          boolean value = false;
          // MADHU
          boolean isNull1 = i.isNullAt(0);
          long value1 = isNull1 ? -1L : (i.getLong(0));
          ${classOf[GenericInternalRow].getName} r = new ${classOf[GenericInternalRow].getName}();
          if (!isNull1) {
            isNull = false; // resultCode could change nullability.
            value = value1 > 30L;
          }
          return !isNull && value;
        }
      }"""

    val code = CodeFormatter.stripOverlappingComments(
      new CodeAndComment(codeBody, ctx.getPlaceHolderToComments()))
    logDebug(s"Generated predicate '$predicate':\n${CodeFormatter.format(code)}")

    val p = CodeGenerator.compile(code).generate(ctx.references.toArray).asInstanceOf[Predicate]
    (r: InternalRow) => p.eval(r)
  }
}

