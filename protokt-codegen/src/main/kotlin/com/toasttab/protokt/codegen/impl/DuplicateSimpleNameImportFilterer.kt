/*
* Copyright (c) 2020 Toast Inc.
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

import arrow.core.None
import arrow.core.Option
import com.toasttab.protokt.codegen.model.Import
import com.toasttab.protokt.codegen.model.PClass
import com.toasttab.protokt.codegen.model.PPackage
import kotlin.reflect.KClass

private val internalPackages = setOf(PPackage.PROTOKT, PPackage.PROTOKT_RT)

fun Sequence<Import>.filterDuplicateSimpleNames(
    pkg: PPackage,
    getClass: (PClass) -> Option<KClass<*>>
): Set<Import> {
    val all = toSet()

    val classImports = all.filterIsInstance<Import.Class>()
    val nonDuplicateImports = mutableSetOf<Import.Class>()

    classImports
        .filterNot { classWithSimpleNameExistsInPackage(it, pkg, getClass) }
        .forEach { import ->
            val withSameName = withSameName(import, nonDuplicateImports)

            if (withSameName.isEmpty()) {
                nonDuplicateImports.add(import)
            } else {
                val duplicate = withSameName.single()
                if (duplicate.pkg in internalPackages) {
                    nonDuplicateImports.add(import)
                    nonDuplicateImports.remove(duplicate)
                }
            }
        }

    return nonDuplicateImports + all.filterNot { it is Import.Class }
}

private fun classWithSimpleNameExistsInPackage(
    import: Import.Class,
    pkg: PPackage,
    getClass: (PClass) -> Option<KClass<*>>
) =
    getClass(PClass(import.pClass.simpleName, pkg, None)).isDefined()

private fun withSameName(
    import: Import.Class,
    imports: Set<Import.Class>
) =
    imports.filter {
        it.pClass.simpleName == import.pClass.simpleName &&
            it.pClass != import.pClass
    }
