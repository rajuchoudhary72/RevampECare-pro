# IntentUtils - Common Utility for Hyperlinks

This document explains how to use the `IntentUtils` utility class and `InfoItemType` for creating clickable email addresses, phone numbers, and URLs across all modules.

## Features

- 🔗 **Clickable Email Addresses** - Opens default email client
- 📞 **Clickable Phone Numbers** - Opens dialer with number
- 🌐 **Clickable URLs** - Opens browser
- 🎯 **Auto-detection** - Automatically detects email or phone from text
- ♻️ **Reusable** - Use across all modules

## Usage in InfoGridView

The easiest way to use this is with `InfoGridView`:

```kotlin
import com.app.ecarepro.designsystem.core.component.InfoGridItem
import com.app.ecarepro.designsystem.core.component.InfoGridView
import com.app.ecarepro.designsystem.core.component.InfoItemType

// In your Composable
val items = listOf(
    InfoGridItem("Name", "John Doe"),  // Regular text
    InfoGridItem("Email", "john@example.com", InfoItemType.EMAIL),  // Clickable email
    InfoGridItem("Phone", "+91 9876543210", InfoItemType.PHONE),   // Clickable phone
    InfoGridItem("Contact", "john@example.com", InfoItemType.AUTO) // Auto-detect
)

InfoGridView(items = items)
```

### InfoItemType Options

- `InfoItemType.TEXT` - Not clickable, regular text (default for backward compatibility)
- `InfoItemType.EMAIL` - Makes the item clickable as email
- `InfoItemType.PHONE` - Makes the item clickable as phone number
- `InfoItemType.AUTO` - Auto-detects if it's email or phone and makes it clickable

## Direct Usage with IntentUtils

You can also use `IntentUtils` directly in your custom components:

```kotlin
import com.app.ecarepro.core.domain.ext.IntentUtils

// In your Composable
val context = LocalContext.current

// Open email
Text(
    text = "john@example.com",
    modifier = Modifier.clickable {
        IntentUtils.openEmail(context, "john@example.com")
    }
)

// Open dialer
Text(
    text = "+91 9876543210",
    modifier = Modifier.clickable {
        IntentUtils.openDialer(context, "+91 9876543210")
    }
)

// Open URL
Text(
    text = "www.example.com",
    modifier = Modifier.clickable {
        IntentUtils.openUrl(context, "www.example.com")
    }
)

// Auto-detect and open
Text(
    text = emailOrPhone,
    modifier = Modifier.clickable {
        IntentUtils.handleAutoLaunch(context, emailOrPhone)
    }
)
```

## Utility Functions

```kotlin
// Check if text is email
if (IntentUtils.isEmail("test@example.com")) {
    // Handle email
}

// Check if text is phone number
if (IntentUtils.isPhoneNumber("+91 9876543210")) {
    // Handle phone
}
```

## Example: Student Profile

```kotlin
// Father's Details
val items = listOf(
    InfoGridItem("Full name", profileData.fatherName ?: "NA"),
    InfoGridItem("Contact no", profileData.fatherMob1 ?: "NA", InfoItemType.PHONE),
    InfoGridItem("Email Id", profileData.fatherEmail1 ?: "NA", InfoItemType.EMAIL),
    InfoGridItem("Address", profileData.fatherAddress ?: "NA")
)
InfoGridView(items = items)
```

## Styling

Clickable items automatically get:
- **Primary color** (instead of text primary)
- **Underline decoration**
- **Click ripple effect**

## Error Handling

The utility handles all edge cases:
- ✅ Empty or null values show toast "No email/phone available"
- ✅ "NA" values are treated as empty
- ✅ Missing apps show toast "No email app found"
- ✅ Exceptions are caught and displayed as toast

## Benefits

1. **Consistent behavior** across all modules
2. **Automatic validation** of email and phone formats
3. **Graceful error handling** with user-friendly messages
4. **Easy to use** - just specify the type
5. **Backward compatible** - existing code works without changes
6. **Accessible** - follows Android accessibility guidelines

## Migration Guide

If you have existing code with email/phone fields:

**Before:**
```kotlin
InfoGridItem("Email", email)
```

**After:**
```kotlin
InfoGridItem("Email", email, InfoItemType.EMAIL)
```

That's it! The field becomes automatically clickable.
