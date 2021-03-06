/*
 * Copyright (c) 2019 Toast Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.toasttab.protokt.codegen.impl

import com.toasttab.protokt.codegen.impl.Deprecation.enclosingDeprecation
import com.toasttab.protokt.codegen.impl.Deprecation.hasDeprecation
import com.toasttab.protokt.codegen.impl.Deprecation.renderOptions
import com.toasttab.protokt.codegen.impl.EnumDocumentationAnnotator.Companion.annotateEnumDocumentation
import com.toasttab.protokt.codegen.impl.EnumDocumentationAnnotator.Companion.annotateEnumFieldDocumentation
import com.toasttab.protokt.codegen.impl.STAnnotator.Context
import com.toasttab.protokt.codegen.protoc.Enum
import com.toasttab.protokt.codegen.template.Enum.Enum as EnumTemplate
import com.toasttab.protokt.codegen.template.Enum.Enum.EnumInfo
import com.toasttab.protokt.codegen.template.Enum.Enum.EnumOptions

internal object EnumAnnotator {
    fun annotateEnum(
        e: Enum,
        ctx: Context
    ) =
        EnumTemplate.render(
            name = e.name,
            map =
                e.values.associate {
                    it.number to
                        EnumInfo(
                            it.valueName,
                            annotateEnumFieldDocumentation(e, it, ctx),
                            if (it.options.default.deprecated) {
                                renderOptions(
                                    it.options.protokt.deprecationMessage
                                )
                            } else {
                                null
                            }
                        )
                },
            options =
                EnumOptions(
                    documentation = annotateEnumDocumentation(e, ctx),
                    deprecation =
                    if (e.options.default.deprecated) {
                        renderOptions(
                            e.options.protokt.deprecationMessage
                        )
                    } else {
                        null
                    },
                    suppressDeprecation =
                    (e.hasDeprecation && !enclosingDeprecation(ctx))
                )
        )
}
