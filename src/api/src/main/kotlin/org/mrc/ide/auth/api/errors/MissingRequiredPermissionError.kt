package org.mrc.ide.auth.api.errors

import org.mrc.ide.auth.models.permissions.ReifiedPermission
import org.mrc.ide.serialization.models.ErrorInfo

class MissingRequiredPermissionError(missingPermissions: Set<ReifiedPermission>) : ApiError(403, listOf(
        ErrorInfo("forbidden", "You do not have sufficient permissions to access this resource. " +
                "Missing these permissions: ${missingPermissions.joinToString(", ")}")
))